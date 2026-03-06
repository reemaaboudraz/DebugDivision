import React, { useState } from "react";
import type { TEvent } from "@/models/Event";

export default function EditEventModal({
  event,
  onSave,
  onClose,
}: {
  event: TEvent;
  onSave: (updated: TEvent) => void;
  onClose: () => void;
}) {
  const [name, setName] = useState(event.name);
  const [location, setLocation] = useState(event.location);
  const [availableTickets, setAvailableTickets] = useState(
    event.availableTickets,
  );
  const [dateOnly, setDateOnly] = useState(
    new Date(event.eventDate.seconds * 1000).toISOString().split("T")[0],
  );

  function handleSubmit(e: React.SyntheticEvent) {
    e.preventDefault();
    const [y, m, d] = dateOnly.split("-").map(Number);
    const eventDateMillis = Date.UTC(y, m - 1, d, 12, 0, 0, 0);
    onSave({
      ...event,
      name,
      location,
      availableTickets,
      eventDate: { seconds: eventDateMillis / 1000, nanos: 0 },
    });
  }

  return (
    <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div className="bg-white rounded-2xl shadow-xl p-8 max-w-md w-full mx-4">
        <h2 className="text-xl text-[#1F2937] mb-6">Edit Event</h2>

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="block text-sm text-[#1F2937] mb-2">
              Event Name
            </label>
            <input
              className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
          </div>
          <div>
            <label className="block text-sm text-[#1F2937] mb-2">
              Location
            </label>
            <input
              className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
              value={location}
              onChange={(e) => setLocation(e.target.value)}
            />
          </div>
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Date</label>
              <input
                type="date"
                className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={dateOnly}
                onChange={(e) => setDateOnly(e.target.value)}
              />
            </div>
            <div>
              <label className="block text-sm text-[#1F2937] mb-2">
                Tickets
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

          <div className="flex gap-3 justify-end pt-2">
            <button
              type="button"
              onClick={onClose}
              className="px-5 py-2 bg-gray-100 text-[#1F2937] rounded-full hover:bg-gray-200 transition-all"
            >
              Cancel
            </button>
            <button
              type="submit"
              className="px-5 py-2 bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB] transition-all"
            >
              Save
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
