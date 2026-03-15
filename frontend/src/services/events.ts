import type {
  CreateEventRequest,
  CreateEventResponse,
  TEvent,
} from "@/models/Event";

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export async function createEvent(
  payload: CreateEventRequest,
): Promise<CreateEventResponse> {
  const res = await fetch(`${API_BASE}/events/create`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `Create event failed (${res.status})`);
  }

  return res.json();
}

export async function getEventById(id: string): Promise<TEvent> {
  const res = await fetch(`${API_BASE}/events/${id}`);

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `Failed to fetch event (${res.status})`);
  }

  return res.json();
}

export async function getAllEvents(): Promise<TEvent[]> {
  const res = await fetch(`${API_BASE}/events`);

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `Failed to fetch events (${res.status})`);
  }

  return res.json();
}

export async function deleteEvent(id: string): Promise<void> {
  const res = await fetch(`${API_BASE}/events/${id}`, {
    method: "DELETE",
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `Delete failed (${res.status})`);
  }
}

export async function updateEvent(
  id: string,
  payload: Partial<CreateEventRequest>,
): Promise<void> {
  const res = await fetch(`${API_BASE}/events/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `Update failed (${res.status})`);
  }
}
