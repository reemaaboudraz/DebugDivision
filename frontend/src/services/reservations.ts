const API_BASE = import.meta.env.VITE_API_BASE_URL ?? "";

export type CreateReservationRequest = {
  eventId: string;
  userName: string;
  userEmail: string;
  numberOfTickets: number;
};

export type ReservationResponse = {
  id: string;
  eventId: string;
  eventName: string;
  userName: string;
  userEmail: string;
  numberOfTickets: number;
  status: string;
  createdAt: { seconds: number; nanos: number };
  eventDate: { seconds: number; nanos: number } | null;
};

export async function createReservation(
  payload: CreateReservationRequest,
): Promise<ReservationResponse> {
  const res = await fetch(`${API_BASE}/reservations`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(payload),
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data.message || `Reservation failed (${res.status})`);
  }

  return res.json();
}

export async function getReservationsByUser(
  email: string,
): Promise<ReservationResponse[]> {
  const res = await fetch(
    `${API_BASE}/reservations/user/${encodeURIComponent(email)}`,
  );

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `Failed to fetch reservations (${res.status})`);
  }

  return res.json();
}

export async function cancelReservation(reservationId: string): Promise<ReservationResponse> {
  const res = await fetch(`${API_BASE}/reservations/${reservationId}/cancel`, {
    method: "PATCH",
  });

  if (!res.ok) {
    const data = await res.json().catch(() => ({}));
    throw new Error(data.message || `Cancellation failed (${res.status})`);
  }

  return res.json();
}