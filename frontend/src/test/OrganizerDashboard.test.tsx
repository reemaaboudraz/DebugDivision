import { describe, it, expect, vi, beforeEach } from "vitest";
import { render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import OrganizerDashboard from "@/pages/OrganizerDashboard";

// --- mocks ---

vi.mock("react-router-dom", () => ({
  useNavigate: () => vi.fn(),
  Link: ({ children, to }: { children: React.ReactNode; to: string }) => (
    <a href={to}>{children}</a>
  ),
}));

vi.mock("@/services/events");
vi.mock("@/components/auth/Profile", () => ({ default: () => <div>Profile</div> }));
vi.mock("@/components/events/EventCard", () => ({
  default: ({ event, onEdit, onDelete }: any) => (
    <div>
      <span>{event.name}</span>
      <button onClick={() => onEdit(event)}>Edit</button>
      <button onClick={() => onDelete(event.id)}>Delete</button>
    </div>
  ),
}));
vi.mock("@/components/events/EditEventModal", () => ({
  default: ({ event, onSave, onClose }: any) => (
    <div>
      <span>Edit Modal - {event.name}</span>
      <button onClick={() => onSave(event)}>Save</button>
      <button onClick={onClose}>Close</button>
    </div>
  ),
}));
vi.mock("@/components/events/DeleteConfirmModal", () => ({
  default: ({ onConfirm, onCancel }: any) => (
    <div>
      <span>Delete Modal</span>
      <button onClick={onConfirm}>Confirm Delete</button>
      <button onClick={onCancel}>Cancel</button>
    </div>
  ),
}));

import { getAllEvents, deleteEvent, updateEvent } from "@/services/events";
import type { TEvent } from "@/models/Event";

const mockGetAllEvents = vi.mocked(getAllEvents);
const mockDeleteEvent = vi.mocked(deleteEvent);
const mockUpdateEvent = vi.mocked(updateEvent);

// --- mock data ---

const mockEvent1: TEvent = {
  id: "event-1",
  name: "Jazz Night",
  location: "Montreal",
  availableTickets: 100,
  eventDate: { seconds: 1893456000, nanos: 0 },
  organizerId: "org-1",
  category: "music",
  venue: "Bell Centre",
};

const mockEvent2: TEvent = {
  id: "event-2",
  name: "Rock Fest",
  location: "Montreal",
  availableTickets: 200,
  eventDate: { seconds: 1893456000, nanos: 0 },
  organizerId: "org-1",
  category: "music",
  venue: "Parc Jean-Drapeau",
};

beforeEach(() => {
  vi.clearAllMocks();
});

// --- tests ---

describe("OrganizerDashboard", () => {
  it("shows loading state while fetching events", () => {
    mockGetAllEvents.mockReturnValue(new Promise(() => {})); // never resolves

    render(<OrganizerDashboard />);

    expect(screen.getByText(/loading events/i)).toBeInTheDocument();
  });

  it("shows error message when fetching events fails", async () => {
    mockGetAllEvents.mockRejectedValue(new Error("Failed to load events."));

    render(<OrganizerDashboard />);

    await waitFor(() => {
      expect(screen.getByText(/failed to load events/i)).toBeInTheDocument();
    });
  });

  it("shows empty state when there are no events", async () => {
    mockGetAllEvents.mockResolvedValue([]);

    render(<OrganizerDashboard />);

    await waitFor(() => {
      expect(screen.getByText(/no events yet/i)).toBeInTheDocument();
    });
  });

  it("displays events after loading", async () => {
    mockGetAllEvents.mockResolvedValue([mockEvent1, mockEvent2]);

    render(<OrganizerDashboard />);

    await waitFor(() => {
      expect(screen.getByText("Jazz Night")).toBeInTheDocument();
      expect(screen.getByText("Rock Fest")).toBeInTheDocument();
    });
  });

  it("opens delete modal when delete is clicked", async () => {
    mockGetAllEvents.mockResolvedValue([mockEvent1]);

    render(<OrganizerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /delete/i }));

    expect(screen.getByText(/delete modal/i)).toBeInTheDocument();
  });

  it("deletes event after confirming in modal", async () => {
    mockGetAllEvents.mockResolvedValue([mockEvent1]);
    mockDeleteEvent.mockResolvedValue(undefined as any);

    render(<OrganizerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /delete/i }));
    await userEvent.click(screen.getByRole("button", { name: /confirm delete/i }));

    await waitFor(() => {
      expect(screen.queryByText("Jazz Night")).not.toBeInTheDocument();
    });

    expect(mockDeleteEvent).toHaveBeenCalledWith("event-1");
  });

  it("closes delete modal when cancel is clicked", async () => {
    mockGetAllEvents.mockResolvedValue([mockEvent1]);

    render(<OrganizerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /delete/i }));
    await userEvent.click(screen.getByRole("button", { name: /^cancel$/i }));

    expect(screen.queryByText(/delete modal/i)).not.toBeInTheDocument();
  });

  it("restores event if delete fails", async () => {
    mockGetAllEvents.mockResolvedValue([mockEvent1]);
    mockDeleteEvent.mockRejectedValue(new Error("Failed to delete event."));

    render(<OrganizerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /delete/i }));
    await userEvent.click(screen.getByRole("button", { name: /confirm delete/i }));

    await waitFor(() => {
      expect(screen.getByText("Jazz Night")).toBeInTheDocument();
    });
  });

  it("opens edit modal when edit is clicked", async () => {
    mockGetAllEvents.mockResolvedValue([mockEvent1]);

    render(<OrganizerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /edit/i }));

    expect(screen.getByText(/edit modal - jazz night/i)).toBeInTheDocument();
  });

  it("saves edited event and closes modal", async () => {
    mockGetAllEvents.mockResolvedValue([mockEvent1]);
    mockUpdateEvent.mockResolvedValue(undefined as any);

    render(<OrganizerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /edit/i }));
    await userEvent.click(screen.getByRole("button", { name: /save/i }));

    await waitFor(() => {
      expect(screen.queryByText(/edit modal/i)).not.toBeInTheDocument();
    });

    expect(mockUpdateEvent).toHaveBeenCalledWith("event-1", expect.any(Object));
  });

  it("closes edit modal when close is clicked", async () => {
    mockGetAllEvents.mockResolvedValue([mockEvent1]);

    render(<OrganizerDashboard />);

    await waitFor(() => screen.getByText("Jazz Night"));
    await userEvent.click(screen.getByRole("button", { name: /edit/i }));
    await userEvent.click(screen.getByRole("button", { name: /close/i }));

    expect(screen.queryByText(/edit modal/i)).not.toBeInTheDocument();
  });
});
