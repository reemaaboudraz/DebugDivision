import { describe, it, expect, vi, beforeEach } from "vitest";
import emailjs from "@emailjs/browser";
import {
  sendReservationConfirmationEmail,
  sendCancellationConfirmationEmail,
} from "@/services/emailConfirmationService";

vi.mock("@emailjs/browser", () => ({
  default: { send: vi.fn() },
}));

const mockEmailjsSend = vi.mocked(emailjs.send);

const mockReservation = {
  id: "res-001",
  userName: "John Doe",
  userEmail: "john@example.com",
  eventName: "Jazz Night",
  eventDate: { seconds: 1893456000, nanos: 0 },
  status: "ACTIVE",
};

beforeEach(() => {
  vi.clearAllMocks();
});

describe("emailConfirmationService", () => {
  describe("sendReservationConfirmationEmail", () => {
    it("calls emailjs.send once", async () => {
      mockEmailjsSend.mockResolvedValue({} as any);

      await sendReservationConfirmationEmail(mockReservation);

      expect(mockEmailjsSend).toHaveBeenCalledTimes(1);
    });

    it("sends correct template params", async () => {
      mockEmailjsSend.mockResolvedValue({} as any);

      await sendReservationConfirmationEmail(mockReservation);

      expect(mockEmailjsSend).toHaveBeenCalledWith(
        expect.any(String),
        expect.any(String),
        expect.objectContaining({
          user_name: "John Doe",
          user_email: "john@example.com",
          event_name: "Jazz Night",
          reservation_id: "res-001",
          reservation_status: "ACTIVE",
        }),
        expect.any(String)
      );
    });

    it("formats Firestore timestamp eventDate correctly", async () => {
      mockEmailjsSend.mockResolvedValue({} as any);

      await sendReservationConfirmationEmail(mockReservation);

      const params = mockEmailjsSend.mock.calls[0][2];
      expect(params.event_date).toBe(new Date(1893456000 * 1000).toLocaleString());
    });

    it("propagates emailjs errors", async () => {
      mockEmailjsSend.mockRejectedValue(new Error("EmailJS failed"));

      await expect(
        sendReservationConfirmationEmail(mockReservation)
      ).rejects.toThrow("EmailJS failed");
    });
  });

  describe("sendCancellationConfirmationEmail", () => {
    it("calls emailjs.send once", async () => {
      mockEmailjsSend.mockResolvedValue({} as any);

      await sendCancellationConfirmationEmail({ ...mockReservation, status: "CANCELLED" });

      expect(mockEmailjsSend).toHaveBeenCalledTimes(1);
    });

    it("sends correct template params for cancellation", async () => {
      mockEmailjsSend.mockResolvedValue({} as any);

      await sendCancellationConfirmationEmail({ ...mockReservation, status: "CANCELLED" });

      expect(mockEmailjsSend).toHaveBeenCalledWith(
        expect.any(String),
        expect.any(String),
        expect.objectContaining({
          user_name: "John Doe",
          user_email: "john@example.com",
          event_name: "Jazz Night",
          reservation_id: "res-001",
          reservation_status: "CANCELLED",
        }),
        expect.any(String)
      );
    });

    it("propagates emailjs errors", async () => {
      mockEmailjsSend.mockRejectedValue(new Error("EmailJS failed"));

      await expect(
        sendCancellationConfirmationEmail(mockReservation)
      ).rejects.toThrow("EmailJS failed");
    });
  });
});