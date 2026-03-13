import { describe, it, expect, vi, beforeEach } from "vitest";
import { createReservation, getReservationsByUser } from "./reservations";
import type { CreateReservationRequest, ReservationResponse } from "./reservations";

const mockPayload: CreateReservationRequest = {
  eventId: "event-1",
  userName: "John Doe",
  userEmail: "john@example.com",
  numberOfTickets: 2,
};

const mockReservation: ReservationResponse = {
  id: "res-1",
  eventId: "event-1",
  eventName: "Test Concert",
  userName: "John Doe",
  userEmail: "john@example.com",
  numberOfTickets: 2,
  status: "CONFIRMED",
  createdAt: { seconds: 1893456000, nanos: 0 },
};

beforeEach(() => {
  vi.restoreAllMocks();
});

describe("createReservation", () => {
  it("returns reservation on success", async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve(mockReservation),
    });

    const result = await createReservation(mockPayload);

    expect(result).toEqual(mockReservation);
    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining("/reservations"),
      expect.objectContaining({
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(mockPayload),
      }),
    );
  });

  it("throws error with server message on failure", async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: false,
      status: 400,
      json: () => Promise.resolve({ message: "Not enough tickets available." }),
    });

    await expect(createReservation(mockPayload)).rejects.toThrow(
      "Not enough tickets available.",
    );
  });

  it("throws fallback error with status code when no message in body", async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: false,
      status: 500,
      json: () => Promise.reject(new Error("parse error")),
    });

    await expect(createReservation(mockPayload)).rejects.toThrow(
      "Reservation failed (500)",
    );
  });
});

describe("getReservationsByUser", () => {
  it("returns list of reservations on success", async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve([mockReservation]),
    });

    const result = await getReservationsByUser("john@example.com");

    expect(result).toHaveLength(1);
    expect(result[0].id).toBe("res-1");
    expect(fetch).toHaveBeenCalledWith(
      expect.stringContaining("/reservations/user/john%40example.com"),
    );
  });

  it("returns empty array when user has no reservations", async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      json: () => Promise.resolve([]),
    });

    const result = await getReservationsByUser("nobody@example.com");

    expect(result).toHaveLength(0);
  });

  it("throws error on failure response", async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: false,
      status: 500,
      text: () => Promise.resolve("Internal Server Error"),
    });

    await expect(getReservationsByUser("john@example.com")).rejects.toThrow(
      "Internal Server Error",
    );
  });

  it("throws fallback error when response body is empty", async () => {
    global.fetch = vi.fn().mockResolvedValue({
      ok: false,
      status: 503,
      text: () => Promise.resolve(""),
    });

    await expect(getReservationsByUser("john@example.com")).rejects.toThrow(
      "Failed to fetch reservations (503)",
    );
  });
});