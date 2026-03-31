import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ReservationPage from "@/pages/ReservationPage";

// --- module mocks ---

const mockNavigate = vi.fn();

const { mockUseAuth } = vi.hoisted(() => ({
  mockUseAuth: vi.fn(),
}));

vi.mock("react-router-dom", () => ({
  useNavigate: () => mockNavigate,
  useParams: () => ({ eventId: "event-1" }),
}));

vi.mock("@/AuthContext", () => ({
  useAuth: () => mockUseAuth(),
}));

vi.mock("@/services/events");
vi.mock("@/services/reservations");
vi.mock("@/services/emailConfirmationService");

import { getEventById } from "@/services/events";
import { createReservation } from "@/services/reservations";
import { sendReservationConfirmationEmail } from "@/services/emailConfirmationService";
import type { TEvent } from "@/models/Event";
import type { ReservationResponse } from "@/services/reservations";

const mockGetEventById = vi.mocked(getEventById);
const mockCreateReservation = vi.mocked(createReservation);
const mockSendEmail = vi.mocked(sendReservationConfirmationEmail);

// --- helpers ---

const mockEvent: TEvent = {
  id: "event-1",
  name: "Test Concert",
  location: "Bell Centre",
  availableTickets: 10,
  eventDate: { seconds: 1893456000, nanos: 0 },
  organizerId: "org-1",
  category: "concert",
  venue: "Bell Centre",
};

const mockReservationResponse: ReservationResponse = {
  id: "res-abc123",
  eventId: "event-1",
  eventName: "Test Concert",
  userName: "John Doe",
  userEmail: "john@example.com",
  numberOfTickets: 2,
  status: "CONFIRMED",
  createdAt: { seconds: 1893456000, nanos: 0 },
};

function setUser() {
  mockUseAuth.mockReturnValue({
    uid: "user-1",
    userProfile: { email: "john@example.com", name: "John Doe" },
  });
}

// --- helper to get to success screen ---

async function reserveTicket() {
  render(<ReservationPage />);
  await waitFor(() => screen.getByText("Test Concert"));
  await userEvent.type(screen.getByPlaceholderText(/enter your full name/i), "John Doe");
  await userEvent.click(screen.getByRole("button", { name: /confirm reservation/i }));
  await waitFor(() => screen.getByText(/reservation confirmed/i));
}

// --- tests ---

beforeEach(() => {
  vi.clearAllMocks();
  setUser();
  mockGetEventById.mockResolvedValue(mockEvent);
  mockCreateReservation.mockResolvedValue(mockReservationResponse);
});

describe("ReservationPage - email confirmation", () => {
  it("shows 'Send email confirmation' button after successful reservation", async () => {
    await reserveTicket();

    expect(
      screen.getByRole("button", { name: /send email confirmation/i })
    ).toBeInTheDocument();
  });

  it("sends confirmation email and shows success message when button is clicked", async () => {
    mockSendEmail.mockResolvedValue({} as any);

    await reserveTicket();
    await userEvent.click(screen.getByRole("button", { name: /send email confirmation/i }));

    await waitFor(() => {
      expect(screen.getByText(/confirmation email sent successfully/i)).toBeInTheDocument();
    });

    expect(mockSendEmail).toHaveBeenCalledWith(expect.objectContaining({
      id: "res-abc123",
      eventName: "Test Concert",
      status: "ACTIVE",
    }));
  });

  it("shows failure message when confirmation email fails", async () => {
    mockSendEmail.mockRejectedValue(new Error("EmailJS error"));

    await reserveTicket();
    await userEvent.click(screen.getByRole("button", { name: /send email confirmation/i }));

    await waitFor(() => {
      expect(screen.getByText(/failed to send confirmation email/i)).toBeInTheDocument();
    });
  });

  it("disables email button while sending", async () => {
    mockSendEmail.mockReturnValue(new Promise(() => {})); // never resolves

    await reserveTicket();
    await userEvent.click(screen.getByRole("button", { name: /send email confirmation/i }));

    await waitFor(() => {
      expect(screen.getByRole("button", { name: /sending/i })).toBeDisabled();
    });
  });
});