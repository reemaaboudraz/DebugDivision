import { useState } from "react";
import { createEvent } from "@/services/events";
import type { CreateEventRequest } from "@/models/Event";
import { TicketPlus } from "lucide-react";
import { Link, useNavigate } from "react-router-dom";

// Convert yyyy-mm-dd (from <input type="date">) to noon UTC in millis
function dateOnlyToNoonUtcMillis(dateStr: string): number {
  // dateStr example: "2026-03-02"
  const [y, m, d] = dateStr.split("-").map(Number);
  if (!y || !m || !d) return 0;
  return Date.UTC(y, m - 1, d, 12, 0, 0, 0); // noon UTC
}

export default function CreateEventPage() {
  const nav = useNavigate();
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [createdId, setCreatedId] = useState<string | null>(null);

  const [name, setName] = useState("");
  const [location, setLocation] = useState("");
  const [availableTickets, setAvailableTickets] = useState<number>(0);
  const [dateOnly, setDateOnly] = useState(""); 

  async function onSubmit(e: React.FormEvent) {
    e.preventDefault();
    setError(null);
    setCreatedId(null);

    if (!name.trim()) return setError("Event name is required.");
    if (!dateOnly) return setError("Event date is required.");
    if (availableTickets < 0) return setError("Available tickets must be 0 or more.");

    const eventDateMillis = dateOnlyToNoonUtcMillis(dateOnly);
    if (!eventDateMillis) return setError("Invalid date.");

    const payload: CreateEventRequest = {
      name: name.trim(),
      location: location.trim(),
      availableTickets,
      eventDateMillis,
      organizerId: "temp-organizer", // TODO: replace when auth exists
    };

    setSubmitting(true);
    try {
      const res = await createEvent(payload);
      setCreatedId(res.id);
      nav("/organizer/dashboard", { replace: true });
    } catch (err: any) {
      setError(err?.message ?? "Failed to create event.");
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="max-w-3xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="mb-8">
        <div className="flex items-center gap-4">
          <div className="bg-[#3B82F6] p-4 rounded-2xl shadow-lg">
            <TicketPlus className="w-10 h-10 text-white" />
          </div>
          <div>
            <h1 className="text-4xl text-[#1F2937]">Create Event</h1>
            <p className="text-[#6B7280] mt-1">
              Add a new event to Firestore.
            </p>
          </div>
        </div>
      </div>

      <form
        onSubmit={onSubmit}
        className="bg-white rounded-2xl p-6 shadow border border-gray-100 space-y-6"
      >
        {error ? (
          <div className="bg-red-50 text-red-700 border border-red-100 rounded-xl p-3">
            {error}
          </div>
        ) : null}

        {createdId ? (
          <div className="bg-green-50 text-green-700 border border-green-100 rounded-xl p-3">
            Event created! id: {createdId}
          </div>
        ) : null}

        <div>
          <label className="block text-sm text-[#1F2937] mb-2">Event Name</label>
          <input
            className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="e.g. Concordia Hack Night"
          />
        </div>

        <div>
          <label className="block text-sm text-[#1F2937] mb-2">Location</label>
          <input
            className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
            value={location}
            onChange={(e) => setLocation(e.target.value)}
            placeholder="e.g. Hall Building, H-110"
          />
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
          <div>
            <label className="block text-sm text-[#1F2937] mb-2">Event Date</label>
            <input
              type="date"
              className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
              value={dateOnly}
              onChange={(e) => setDateOnly(e.target.value)}
            />
          </div>

          <div>
            <label className="block text-sm text-[#1F2937] mb-2">
              Available Tickets
            </label>
            <input
              type="number"
              min={0}
              className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
              value={availableTickets}
              onChange={(e) => setAvailableTickets(Number(e.target.value))}
            />
          </div>
        </div>

        <div className="flex flex-wrap gap-4">
          <button
            type="submit"
            disabled={submitting}
            className="px-8 py-4 bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB] hover:shadow-lg hover:scale-105 transition-all text-lg disabled:opacity-60 disabled:hover:scale-100"
          >
            {submitting ? "Creating..." : "Create Event"}
          </button>

          <Link
            to="/organizer/dashboard"
            className="px-8 py-4 bg-gray-100 text-[#1F2937] rounded-full hover:shadow-lg hover:scale-105 transition-all text-lg"
          >
            Cancel
          </Link>
        </div>
      </form>
    </div>
  );
}