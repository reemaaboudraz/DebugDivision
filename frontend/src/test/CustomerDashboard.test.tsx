import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import CustomerDashboard from "@/pages/CustomerDashboard";

const mockNavigate = vi.fn();
const { mockUseAuth } = vi.hoisted(() => ({ mockUseAuth: vi.fn() }));

vi.mock("react-router-dom", () => ({
  useNavigate: () => mockNavigate,
}));

vi.mock("@/AuthContext", () => ({
  useAuth: () => mockUseAuth(),
}));

vi.mock("@/services/reservations");
vi.mock("@/services/emailConfirmationService");
vi.mock("@/components/auth/Profile", () => ({ default: () => <div>Profile</div> }));

import { getReservationsByUser, cancelReservation } from "@/services/reservations";
import { sendCancellationConfirmationEmail } from "@/services/emailConfirmationService";
import type { ReservationResponse } from "@/services/reservations";

const mockGetReservations = vi.mocked(getReservationsByUser);
const mockCancelReservation = vi.mocked(cancelReservation);
const mockSendCancellationEmail = vi.mocked(sendCancellationConfirmationEmail);

const mockActiveReservation: ReservationResponse = {
  id: "res-001",
  eventId: "event-1",
  eventName: "Jazz Night",
  userName: "Jane Doe",
  userEmail: "jane@example.com",
  numberOfTickets: 2,
  status: "CONFIRMED",
  createdAt: { seconds: 1893456000, nanos: 0 },
  eventDate: { seconds: 1893456000, nanos: 0 },
};

function setUser() {
  mockUseAuth.mockReturnValue({
    uid: "user-1",
    userProfile: { email: "jane@example.com", name: "Jane Doe" },
  });
}

beforeEach(() => {
  vi.clearAllMocks();
  setUser();
});

describe("CustomerDashboard", () => {
  it("shows active reservations", async () => {
    mockGetReservations.mockResolvedValue([mockActiveReservation]);

    render(<CustomerDashboard />);

    await waitFor(() => {
      expect(screen.getByText("Jazz Night")).toBeInTheDocument();
    });
  });

  it("shows cancel button only for active reservations", async () => {
    const cancelled: ReservationResponse = {
        ...mockActiveReservation,
        id: "res-002",
        status: "CANCELLED",
        eventName: "Jazz Night", 
    };
    
    mockGetReservations.mockResolvedValue([mockActiveReservation, cancelled]);

    render(<CustomerDashboard />);

    await waitFor(() => {
        expect(screen.getAllByText("Jazz Night")).toHaveLength(2); // both cards render
    });

    expect(screen.getAllByRole("button", { name: /cancel reservation/i })).toHaveLength(1);
    });

  it("opens cancel modal when cancel button is clicked", async () => {
    mockGetReservations.mockResolvedValue([mockActiveReservation]);

    render(<CustomerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /cancel reservation/i }));

    expect(screen.getByText(/this action cannot be undone/i)).toBeInTheDocument();
  });

  it("sends cancellation email automatically after confirming cancel", async () => {
    mockGetReservations.mockResolvedValue([mockActiveReservation]);
    mockCancelReservation.mockResolvedValue({ ...mockActiveReservation, status: "CANCELLED" });
    mockSendCancellationEmail.mockResolvedValue({} as any);

    render(<CustomerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /cancel reservation/i }));
    await userEvent.click(screen.getByRole("button", { name: /yes, cancel/i }));

    await waitFor(() => {
      expect(mockSendCancellationEmail).toHaveBeenCalledWith(expect.objectContaining({
        id: "res-001",
        eventName: "Jazz Night",
        status: "CANCELLED",
      }));
    });
  });

  it("closes modal after successful cancellation", async () => {
    mockGetReservations.mockResolvedValue([mockActiveReservation]);
    mockCancelReservation.mockResolvedValue({ ...mockActiveReservation, status: "CANCELLED" });
    mockSendCancellationEmail.mockResolvedValue({} as any);

    render(<CustomerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /cancel reservation/i }));
    await userEvent.click(screen.getByRole("button", { name: /yes, cancel/i }));

    await waitFor(() => {
      expect(screen.queryByText(/this action cannot be undone/i)).not.toBeInTheDocument();
    });
  });

  it("shows error in modal when cancellation fails", async () => {
    mockGetReservations.mockResolvedValue([mockActiveReservation]);
    mockCancelReservation.mockRejectedValue(new Error("Cancellation failed."));

    render(<CustomerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /cancel reservation/i }));
    await userEvent.click(screen.getByRole("button", { name: /yes, cancel/i }));

    await waitFor(() => {
      expect(screen.getByText(/cancellation failed/i)).toBeInTheDocument();
    });
  });

  it("redirects to login when not authenticated", async () => {
    mockUseAuth.mockReturnValue({ uid: null, userProfile: null });

    render(<CustomerDashboard />);

    await waitFor(() => {
      expect(mockNavigate).toHaveBeenCalledWith("/login");
    });
  });
});