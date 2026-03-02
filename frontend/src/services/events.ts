import type { CreateEventRequest, CreateEventResponse } from "@/models/Event";

const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export async function createEvent(
  payload: CreateEventRequest
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