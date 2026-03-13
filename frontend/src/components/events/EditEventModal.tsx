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
const [category, setCategory] = useState(event.category ?? "movie");
const [artist, setArtist] = useState(event.artist ?? "");
const [organization, setOrganization] = useState(event.organization ?? "");
const [city, setCity] = useState(event.city ?? "");
const [country, setCountry] = useState(event.country ?? "");
const [overview, setOverview] = useState(event.overview ?? "");
const [venue, setVenue] = useState(event.venue ?? "");
const [imageUrl, setImageUrl] = useState(event.imageUrl ?? "");
const [buyTicketsUrl, setBuyTicketsUrl] = useState(event.buyTicketsUrl ?? "");

  function handleSubmit(e: React.SyntheticEvent) {
    e.preventDefault();
    const [y, m, d] = dateOnly.split("-").map(Number);
    const eventDateMillis = Date.UTC(y, m - 1, d, 12, 0, 0, 0);
    onSave({
      ...event,
      name,
      location,
      availableTickets,
      category,
      artist,
      organization,
      city,
      country,
      overview,
      venue,
      imageUrl,
      buyTicketsUrl,
      eventDate: { seconds: eventDateMillis / 1000, nanos: 0 },
    });
  }

  return (
    <div className="fixed inset-0 z-50 bg-black/50 flex items-center justify-center p-4">
      <div className="bg-white w-full max-w-2xl rounded-3xl shadow-2xl max-h-[90vh] flex flex-col">

        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b border-gray-200">
          <h2 className="text-2xl text-[#1F2937]">Edit Event</h2>
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 rounded-full bg-gray-100 hover:bg-gray-200 transition"
          >
            Close
          </button>
        </div>

        {/* Scrollable content */}
        <form onSubmit={handleSubmit} className="flex flex-col min-h-0">
          <div className="overflow-y-auto px-6 py-5 space-y-5 min-h-0">
            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Event Name</label>
              <input
                className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={name}
                onChange={(e) => setName(e.target.value)}
              />
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Location</label>
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
                <label className="block text-sm text-[#1F2937] mb-2">Tickets</label>
                <input
                  type="number"
                  min={0}
                  className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                  value={availableTickets}
                  onChange={(e) => setAvailableTickets(Number(e.target.value))}
                />
              </div>
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Category</label>
              <select
                className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={category}
                onChange={(e) =>
                  setCategory(e.target.value as "movie" | "concert" | "sports" | "travel")
                }
              >
                <option value="movie">Movie</option>
                <option value="concert">Concert</option>
                <option value="sports">Sports</option>
                <option value="travel">Travel</option>
              </select>
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Artist</label>
              <input
                className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={artist}
                onChange={(e) => setArtist(e.target.value)}
              />
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Organization</label>
              <input
                className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={organization}
                onChange={(e) => setOrganization(e.target.value)}
              />
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm text-[#1F2937] mb-2">City</label>
                <input
                  className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                  value={city}
                  onChange={(e) => setCity(e.target.value)}
                />
              </div>

              <div>
                <label className="block text-sm text-[#1F2937] mb-2">Country</label>
                <input
                  className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                  value={country}
                  onChange={(e) => setCountry(e.target.value)}
                />
              </div>
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Venue</label>
              <input
                className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={venue}
                onChange={(e) => setVenue(e.target.value)}
              />
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Image URL</label>
              <input
                className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={imageUrl}
                onChange={(e) => setImageUrl(e.target.value)}
              />
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Buy Tickets URL</label>
              <input
                className="w-full rounded-xl border border-gray-200 px-4 py-3 focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={buyTicketsUrl}
                onChange={(e) => setBuyTicketsUrl(e.target.value)}
              />
            </div>

            <div>
              <label className="block text-sm text-[#1F2937] mb-2">Overview</label>
              <textarea
                className="w-full rounded-xl border border-gray-200 px-4 py-3 min-h-32 focus:outline-none focus:ring-2 focus:ring-blue-200"
                value={overview}
                onChange={(e) => setOverview(e.target.value)}
              />
            </div>
          </div>

          {/* Sticky footer actions */}
          <div className="border-t border-gray-200 px-6 py-4 bg-white flex justify-end gap-3">
            <button
              type="button"
              onClick={onClose}
              className="px-5 py-3 rounded-full bg-gray-100 hover:bg-gray-200 transition"
            >
              Discard
            </button>
            <button
              type="submit"
              className="px-5 py-3 rounded-full bg-[#3B82F6] text-white hover:bg-[#2563EB] transition"
            >
              Save Changes
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
