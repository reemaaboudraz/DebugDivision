import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { TicketPlus } from "lucide-react";
import { getAllEvents, deleteEvent, updateEvent } from "@/services/events";
import type { TEvent } from "@/models/Event";
import EventCard from "@/components/events/EventCard";
import EditEventModal from "@/components/events/EditEventModal";
import DeleteConfirmModal from "@/components/events/DeleteConfirmModal";

export default function OrganizerDashboard() {
  const [events, setEvents] = useState<TEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [pendingDeleteId, setPendingDeleteId] = useState<string | null>(null);
  const [editingEvent, setEditingEvent] = useState<TEvent | null>(null);

  useEffect(() => {
    getAllEvents()
      .then(setEvents)
      .catch((err) => setError(err?.message ?? "Failed to load events."))
      .finally(() => setLoading(false));
  }, []);

  async function handleDelete(id: string) {
    const eventToDelete = events.find((e) => e.id === id);
    setPendingDeleteId(null);
    setEvents((prev) => prev.filter((e) => e.id !== id));
    try {
      await deleteEvent(id);
    } catch (err: any) {
      setEvents((prev) => [...prev, eventToDelete!]);
      setError(err?.message ?? "Failed to delete event.");
    }
  }

  async function handleSaveEdit(updated: TEvent) {
    try {
      await updateEvent(updated.id, {
        name: updated.name,
        location: updated.location,
        availableTickets: updated.availableTickets,
        eventDateMillis: updated.eventDate.seconds * 1000,
        category: updated.category,
        artist: updated.artist,
        organization: updated.organization,
        city: updated.city,
        country: updated.country,
        overview: updated.overview,
        venue: updated.venue,
        imageUrl: updated.imageUrl,
        buyTicketsUrl: updated.buyTicketsUrl,
      });
      setEvents((prev) => prev.map((e) => (e.id === updated.id ? updated : e)));
      setEditingEvent(null);
    } catch (err: any) {
      setError(err?.message ?? "Failed to update event.");
    }
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div className="bg-white rounded-3xl shadow-lg p-8 border border-gray-200">
        {/* Header */}
        <div className="flex items-center gap-4 mb-6">
          <div className="bg-[#3B82F6] p-4 rounded-2xl shadow-lg">
            <TicketPlus className="w-10 h-10 text-white" />
          </div>
          <div>
            <h1 className="text-4xl text-[#1F2937]">Organizer Dashboard</h1>
            <p className="text-[#6B7280] mt-1">Create and manage events.</p>
          </div>
        </div>

        <div className="flex gap-4 flex-wrap">
          <Link
            to="/organizer/events/create"
            className="px-8 py-4 bg-[#10B981] text-white rounded-full hover:bg-[#059669] hover:shadow-lg hover:scale-105 transition-all text-lg"
          >
            Create Event
          </Link>

          <Link
            to="/"
            className="px-8 py-4 bg-gray-100 text-[#1F2937] rounded-full hover:shadow-lg hover:scale-105 transition-all text-lg"
          >
            Back to Home
          </Link>
        </div>
        {loading && (
          <p className="text-[#6B7280] text-center py-12">Loading events...</p>
        )}

        {error && (
          <div className="bg-red-50 text-red-700 border border-red-100 rounded-xl p-4 mt-6">
            {error}
          </div>
        )}

        {!loading && !error && events.length === 0 && (
          <p className="text-[#6B7280] text-center py-12">
            No events yet.{" "}
            <Link
              to="/organizer/events/create"
              className="text-[#3B82F6] hover:underline"
            >
              Create your first one!
            </Link>
          </p>
        )}

        {!loading && !error && events.length > 0 && (
          <div className="flex flex-col gap-4 mt-8">
            {events.map((event) => (
              <EventCard
                key={event.id}
                event={event}
                onEdit={setEditingEvent}
                onDelete={setPendingDeleteId}
              />
            ))}
          </div>
        )}
      </div>

      {pendingDeleteId && (
        <DeleteConfirmModal
          onConfirm={() => handleDelete(pendingDeleteId)}
          onCancel={() => setPendingDeleteId(null)}
        />
      )}

      {editingEvent && (
        <EditEventModal
          event={editingEvent}
          onSave={handleSaveEdit}
          onClose={() => setEditingEvent(null)}
        />
      )}
    </div>
  );
}
