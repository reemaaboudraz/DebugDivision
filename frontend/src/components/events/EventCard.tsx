import { MapPin, Calendar, Ticket } from "lucide-react";
import type { TEvent } from "@/models/Event";

export default function EventCard({
  event,
  onEdit,
  onDelete,
}: {
  event: TEvent;
  onEdit: (event: TEvent) => void;
  onDelete: (id: string) => void;
}) {
  return (
    <div className="border border-gray-100 rounded-2xl p-5 hover:shadow-md transition-all">
      <h3 className="text-lg text-[#1F2937] mb-3">{event.name}</h3>

      <div className="space-y-2 text-sm text-[#6B7280]">
        <div className="flex items-center gap-2">
          <MapPin className="w-4 h-4" />
          <span>{event.location || "No location set"}</span>
        </div>
        <div className="flex items-center gap-2">
          <Calendar className="w-4 h-4" />
          <span>
            {new Date(event.eventDate.seconds * 1000).toLocaleDateString()}
          </span>
        </div>
        <div className="flex items-center gap-2">
          <Ticket className="w-4 h-4" />
          <span>{event.availableTickets} tickets available</span>
        </div>
      </div>

      <div className="flex gap-3 mt-4">
        <button
          onClick={() => onEdit(event)}
          className="px-4 py-2 text-sm bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB] transition-all"
        >
          Edit
        </button>
        <button
          onClick={() => onDelete(event.id)}
          className="px-4 py-2 text-sm bg-red-100 text-red-600 rounded-full hover:bg-red-200 transition-all"
        >
          Delete
        </button>
      </div>
    </div>
  );
}
