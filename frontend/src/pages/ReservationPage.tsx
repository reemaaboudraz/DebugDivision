import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { Calendar, MapPin, Ticket } from "lucide-react";
import { getEventById } from "@/services/events";
import { createReservation } from "@/services/reservations";
import type { TEvent } from "@/models/Event";

function getCurrentUser(): { email: string } | null {
  try {
    return JSON.parse(localStorage.getItem("tixy_user") ?? "null");
  } catch {
    return null;
  }
}

function formatEventDate(event: TEvent) {
  return new Date(event.eventDate.seconds * 1000).toLocaleString();
}

export default function ReservationPage() {
  const { eventId } = useParams<{ eventId: string }>();
  const navigate = useNavigate();

  const currentUser = getCurrentUser();

  const [event, setEvent] = useState<TEvent | null>(null);
  const [loadingEvent, setLoadingEvent] = useState(true);
  const [eventError, setEventError] = useState<string | null>(null);

  const [userName, setUserName] = useState("");
  const [userEmail, setUserEmail] = useState(currentUser?.email ?? "");
  const [numberOfTickets, setNumberOfTickets] = useState(1);

  const [submitting, setSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);
  const [reservationId, setReservationId] = useState<string | null>(null);

  useEffect(() => {
    if (!currentUser) {
      navigate("/login");
      return;
    }
    if (!eventId) return;

    getEventById(eventId)
      .then(setEvent)
      .catch((err) => setEventError(err?.message ?? "Failed to load event."))
      .finally(() => setLoadingEvent(false));
  }, [eventId]);

  async function handleSubmit(e: React.FormEvent) {
    e.preventDefault();
    if (!event || !eventId) return;

    setSubmitting(true);
    setSubmitError(null);

    try {
      const res = await createReservation({
        eventId,
        userName: userName.trim(),
        userEmail: userEmail.trim(),
        numberOfTickets,
      });
      setReservationId(res.id);
      setSuccess(true);
    } catch (err: unknown) {
      setSubmitError(err instanceof Error ? err.message : "Reservation failed.");
    } finally {
      setSubmitting(false);
    }
  }

  if (loadingEvent) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-16 text-[#6B7280]">
        Loading event details...
      </div>
    );
  }

  if (eventError || !event) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-16">
        <div className="bg-red-50 text-red-700 border border-red-100 rounded-xl p-4">
          {eventError ?? "Event not found."}
        </div>
      </div>
    );
  }

  if (success) {
    return (
      <div className="max-w-2xl mx-auto px-4 py-16 text-center">
        <div className="bg-white rounded-3xl shadow-lg border border-gray-100 p-10">
          <div className="text-5xl mb-4">🎉</div>
          <h2 className="text-2xl text-[#1F2937] mb-2">Reservation Confirmed!</h2>
          <p className="text-[#6B7280] mb-1">
            You reserved <strong>{numberOfTickets}</strong> ticket(s) for{" "}
            <strong>{event.name}</strong>.
          </p>
          <p className="text-sm text-[#6B7280] mb-6">
            Confirmation ID: <span className="font-mono">{reservationId}</span>
          </p>
          <div className="flex justify-center gap-3">
            <button
              onClick={() => navigate("/events")}
              className="px-6 py-3 bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB] transition-all"
            >
              Browse More Events
            </button>
            <button
              onClick={() => navigate("/")}
              className="px-6 py-3 bg-gray-100 text-[#1F2937] rounded-full hover:bg-gray-200 transition-all"
            >
              Go Home
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-3xl mx-auto px-4 py-12">
      <button
        onClick={() => navigate(-1)}
        className="text-[#6B7280] hover:text-[#1F2937] mb-6 flex items-center gap-1 transition-colors"
      >
        ← Back
      </button>

      <h1 className="text-3xl text-[#1F2937] mb-8">Reserve Tickets</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        {/* Event Summary */}
        <div className="bg-white rounded-3xl border border-gray-100 shadow-md overflow-hidden">
          <div className="h-48 bg-gray-100">
            <img
              src={event.imageUrl || "https://placehold.co/800x500?text=Event"}
              alt={event.name}
              className="w-full h-full object-cover"
            />
          </div>
          <div className="p-5">
            <h2 className="text-xl text-[#1F2937] mb-3">{event.name}</h2>
            <div className="space-y-2 text-sm text-[#6B7280]">
              <div className="flex items-center gap-2">
                <Calendar className="w-4 h-4" />
                <span>{formatEventDate(event)}</span>
              </div>
              <div className="flex items-center gap-2">
                <MapPin className="w-4 h-4" />
                <span>{event.venue || event.location || "No place specified"}</span>
              </div>
              <div className="flex items-center gap-2">
                <Ticket className="w-4 h-4" />
                <span>{event.availableTickets} tickets available</span>
              </div>
            </div>
          </div>
        </div>

        {/* Reservation Form */}
        <div className="bg-white rounded-3xl border border-gray-100 shadow-md p-6">
          <h2 className="text-xl text-[#1F2937] mb-5">Your Details</h2>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block text-sm text-[#1F2937] mb-1">Full Name</label>
              <input
                type="text"
                required
                value={userName}
                onChange={(e) => setUserName(e.target.value)}
                placeholder="Enter your full name"
                className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#3B82F6] text-[#1F2937] placeholder:text-[#6B7280]"
              />
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-1">Email</label>
              <input
                type="email"
                required
                value={userEmail}
                onChange={(e) => setUserEmail(e.target.value)}
                placeholder="Enter your email"
                className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#3B82F6] text-[#1F2937] placeholder:text-[#6B7280]"
              />
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-1">Number of Tickets</label>
              <input
                type="number"
                required
                min={1}
                max={event.availableTickets}
                value={numberOfTickets}
                onChange={(e) => setNumberOfTickets(Number(e.target.value))}
                className="w-full px-4 py-3 border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-[#3B82F6] text-[#1F2937]"
              />
              <p className="text-xs text-[#6B7280] mt-1">
                Max {event.availableTickets} tickets available
              </p>
            </div>

            {submitError && (
              <div className="bg-red-50 text-red-700 border border-red-100 rounded-xl p-3 text-sm">
                {submitError}
              </div>
            )}

            <button
              type="submit"
              disabled={submitting}
              className="w-full px-6 py-3 bg-[#EC4899] text-white rounded-xl hover:bg-[#DB2777] hover:shadow-lg transition-all disabled:opacity-60 disabled:cursor-not-allowed"
            >
              {submitting ? "Confirming..." : "Confirm Reservation"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}