import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import ReservationPage from "./ReservationPage";

// --- module mocks ---

const mockNavigate = vi.fn();

vi.mock("react-router-dom", () => ({
  useNavigate: () => mockNavigate,
  useParams: () => ({ eventId: "event-1" }),
}));

vi.mock("@/services/events");
vi.mock("@/services/reservations");

import { getEventById } from "@/services/events";
import { createReservation } from "@/services/reservations";
import type { TEvent } from "@/models/Event";
import type { ReservationResponse } from "@/services/reservations";

const mockGetEventById = vi.mocked(getEventById);
const mockCreateReservation = vi.mocked(createReservation);

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

function setUser(email = "john@example.com") {
  localStorage.setItem("tixy_user", JSON.stringify({ email }));
}

function clearUser() {
  localStorage.removeItem("tixy_user");
}

// --- tests ---

beforeEach(() => {
  vi.clearAllMocks();
  clearUser();
});

describe("ReservationPage", () => {
  it("redirects to /login when user is not authenticated", async () => {
    clearUser();
    mockGetEventById.mockResolvedValue(mockEvent);

    render(<ReservationPage />);

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith("/login");
    });
  });

  it("shows loading state while fetching event", () => {
    setUser();
    mockGetEventById.mockReturnValue(new Promise(() => {})); // never resolves

    render(<ReservationPage />);

    expect(screen.getByText(/loading event details/i)).toBeInTheDocument();
  });

  it("renders event details after loading", async () => {
    setUser();
    mockGetEventById.mockResolvedValue(mockEvent);

    render(<ReservationPage />);

    await waitFor(() => {
      expect(screen.getByText("Test Concert")).toBeInTheDocument();
    });

    expect(screen.getAllByText(/10 tickets available/i).length).toBeGreaterThan(0);
    expect(screen.getByText(/Bell Centre/i)).toBeInTheDocument();
  });

  it("shows error message when event fetch fails", async () => {
    setUser();
    mockGetEventById.mockRejectedValue(new Error("Event not found."));

    render(<ReservationPage />);

    await waitFor(() => {
      expect(screen.getByText(/Event not found./i)).toBeInTheDocument();
    });
  });

  it("pre-fills email from localStorage", async () => {
    setUser("jane@example.com");
    mockGetEventById.mockResolvedValue(mockEvent);

    render(<ReservationPage />);

    await waitFor(() => screen.getByText("Test Concert"));

    const emailInput = screen.getByPlaceholderText(/enter your email/i);
    expect(emailInput).toHaveValue("jane@example.com");
  });

  it("shows confirmation screen after successful reservation", async () => {
    setUser();
    mockGetEventById.mockResolvedValue(mockEvent);
    mockCreateReservation.mockResolvedValue(mockReservationResponse);

    render(<ReservationPage />);

    await waitFor(() => screen.getByText("Test Concert"));

    await userEvent.type(
      screen.getByPlaceholderText(/enter your full name/i),
      "John Doe",
    );

    await userEvent.click(
      screen.getByRole("button", { name: /confirm reservation/i }),
    );

    await waitFor(() => {
      expect(screen.getByText(/reservation confirmed/i)).toBeInTheDocument();
    });

    expect(screen.getByText(/res-abc123/)).toBeInTheDocument();
    expect(screen.getByText(/Test Concert/)).toBeInTheDocument();
  });

  it("shows error message when reservation submission fails", async () => {
    setUser();
    mockGetEventById.mockResolvedValue(mockEvent);
    mockCreateReservation.mockRejectedValue(
      new Error("Not enough tickets available."),
    );

    render(<ReservationPage />);

    await waitFor(() => screen.getByText("Test Concert"));

    await userEvent.type(
      screen.getByPlaceholderText(/enter your full name/i),
      "John Doe",
    );

    await userEvent.click(
      screen.getByRole("button", { name: /confirm reservation/i }),
    );

    await waitFor(() => {
      expect(
        screen.getByText(/not enough tickets available/i),
      ).toBeInTheDocument();
    });
  });

  it("disables submit button while submitting", async () => {
    setUser();
    mockGetEventById.mockResolvedValue(mockEvent);
    // never resolves so button stays in loading state
    mockCreateReservation.mockReturnValue(new Promise(() => {}));

    render(<ReservationPage />);

    await waitFor(() => screen.getByText("Test Concert"));

    await userEvent.type(
      screen.getByPlaceholderText(/enter your full name/i),
      "John Doe",
    );

    const submitButton = screen.getByRole("button", {
      name: /confirm reservation/i,
    });
    await userEvent.click(submitButton);

    await waitFor(() => {
      expect(
        screen.getByRole("button", { name: /confirming/i }),
      ).toBeDisabled();
    });
  });

  it("navigates back to events from confirmation screen", async () => {
    setUser();
    mockGetEventById.mockResolvedValue(mockEvent);
    mockCreateReservation.mockResolvedValue(mockReservationResponse);

    render(<ReservationPage />);

    await waitFor(() => screen.getByText("Test Concert"));

    await userEvent.type(
      screen.getByPlaceholderText(/enter your full name/i),
      "John Doe",
    );
    await userEvent.click(
      screen.getByRole("button", { name: /confirm reservation/i }),
    );

    await waitFor(() => screen.getByText(/reservation confirmed/i));

    await userEvent.click(
      screen.getByRole("button", { name: /browse more events/i }),
    );

    expect(mockNavigate).toHaveBeenCalledWith("/events");
  });
});