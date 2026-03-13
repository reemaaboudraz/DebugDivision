import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useSearchParams } from "react-router-dom";
import { Calendar, Heart, MapPin, Ticket, Music, Film, Trophy, Plane } from "lucide-react";
import { getAllEvents } from "@/services/events";
import type { EventCategory, TEvent } from "@/models/Event";

const categories: { key: EventCategory; label: string; icon: JSX.Element }[] = [
  { key: "movie", label: "Movies", icon: <Film className="w-4 h-4" /> },
  { key: "concert", label: "Concerts", icon: <Music className="w-4 h-4" /> },
  { key: "sports", label: "Sports", icon: <Trophy className="w-4 h-4" /> },
  { key: "travel", label: "Travel", icon: <Plane className="w-4 h-4" /> },
];

function formatEventDate(event: TEvent) {
  return new Date(event.eventDate.seconds * 1000).toLocaleString();
}

function matchesDateFilter(event: TEvent, selectedDate: string) {
  if (!selectedDate) return true;
  const eventDate = new Date(event.eventDate.seconds * 1000).toISOString().split("T")[0];
  return eventDate === selectedDate;
}

function getFavoriteIds(): string[] {
  try {
    return JSON.parse(localStorage.getItem("favoriteEvents") ?? "[]");
  } catch {
    return [];
  }
}

function setFavoriteIds(ids: string[]) {
  localStorage.setItem("favoriteEvents", JSON.stringify(ids));
}

function getCurrentUser(): { email: string } | null {
  try {
    return JSON.parse(localStorage.getItem("tixy_user") ?? "null");
  } catch {
    return null;
  }
}

export default function EventsPage() {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useSearchParams();
  const initialCategory = (searchParams.get("category") as EventCategory) || "movie";

  const currentUser = getCurrentUser();
  const isLoggedIn = currentUser !== null;
  const isOrganizer = isLoggedIn && currentUser.email.toLowerCase().endsWith(".org");

  const [activeCategory, setActiveCategory] = useState<EventCategory>(initialCategory);
  const [events, setEvents] = useState<TEvent[]>([]);
  const [selectedEvent, setSelectedEvent] = useState<TEvent | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [dateFilter, setDateFilter] = useState("");
  const [artistFilter, setArtistFilter] = useState("");
  const [organizationFilter, setOrganizationFilter] = useState("");
  const [cityFilter, setCityFilter] = useState("");
  const [countryFilter, setCountryFilter] = useState("");
  const [favorites, setFavorites] = useState<string[]>(getFavoriteIds());

  useEffect(() => {
    setSearchParams({ category: activeCategory });
  }, [activeCategory, setSearchParams]);

  useEffect(() => {
    getAllEvents()
      .then(setEvents)
      .catch((err) => setError(err?.message ?? "Failed to load events."))
      .finally(() => setLoading(false));
  }, []);

  function toggleFavorite(eventId: string) {
    const next = favorites.includes(eventId)
      ? favorites.filter((id) => id !== eventId)
      : [...favorites, eventId];

    setFavorites(next);
    setFavoriteIds(next);
  }

  const filteredEvents = useMemo(() => {
    return events.filter((event) => {
      const categoryMatch = (event.category || "").toLowerCase() === activeCategory;
      const dateMatch = matchesDateFilter(event, dateFilter);
      const artistMatch = !artistFilter || (event.artist ?? "").toLowerCase().includes(artistFilter.toLowerCase());
      const orgMatch = !organizationFilter || (event.organization ?? "").toLowerCase().includes(organizationFilter.toLowerCase());
      const cityMatch = !cityFilter || (event.city ?? "").toLowerCase().includes(cityFilter.toLowerCase());
      const countryMatch = !countryFilter || (event.country ?? "").toLowerCase().includes(countryFilter.toLowerCase());

      return categoryMatch && dateMatch && artistMatch && orgMatch && cityMatch && countryMatch;
    });
  }, [events, activeCategory, dateFilter, artistFilter, organizationFilter, cityFilter, countryFilter]);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
      <div className="mb-10">
        <h1 className="text-4xl text-[#1F2937] mb-2">Browse Events</h1>
        <p className="text-[#6B7280]">
          Explore events by category and filter them by date, artist, organization, city, and country.
        </p>
      </div>

      <div className="flex flex-wrap gap-3 mb-8">
        {categories.map((category) => (
          <button
            key={category.key}
            onClick={() => setActiveCategory(category.key)}
            className={`px-5 py-3 rounded-full flex items-center gap-2 transition-all ${
              activeCategory === category.key
                ? "bg-[#3B82F6] text-white shadow-md"
                : "bg-white text-[#1F2937] border border-gray-200 hover:shadow-md"
            }`}
          >
            {category.icon}
            {category.label}
          </button>
        ))}
      </div>

      <div className="bg-white rounded-3xl shadow-lg border border-gray-100 p-6 mb-8">
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-5 gap-4">
          <input
            type="date"
            value={dateFilter}
            onChange={(e) => setDateFilter(e.target.value)}
            className="rounded-xl border border-gray-200 px-4 py-3"
          />
          <input
            type="text"
            placeholder="Artist"
            value={artistFilter}
            onChange={(e) => setArtistFilter(e.target.value)}
            className="rounded-xl border border-gray-200 px-4 py-3"
          />
          <input
            type="text"
            placeholder="Organization"
            value={organizationFilter}
            onChange={(e) => setOrganizationFilter(e.target.value)}
            className="rounded-xl border border-gray-200 px-4 py-3"
          />
          <input
            type="text"
            placeholder="City"
            value={cityFilter}
            onChange={(e) => setCityFilter(e.target.value)}
            className="rounded-xl border border-gray-200 px-4 py-3"
          />
          <input
            type="text"
            placeholder="Country"
            value={countryFilter}
            onChange={(e) => setCountryFilter(e.target.value)}
            className="rounded-xl border border-gray-200 px-4 py-3"
          />
        </div>

        <div className="mt-4">
          <button
            onClick={() => {
              setDateFilter("");
              setArtistFilter("");
              setOrganizationFilter("");
              setCityFilter("");
              setCountryFilter("");
            }}
            className="px-5 py-2 bg-gray-100 rounded-full hover:bg-gray-200 transition-all"
          >
            Clear Filters
          </button>
        </div>
      </div>

      {loading && <p className="text-[#6B7280]">Loading events...</p>}

      {error && (
        <div className="bg-red-50 text-red-700 border border-red-100 rounded-xl p-4">
          {error}
        </div>
      )}

      {!loading && !error && filteredEvents.length === 0 && (
        <div className="bg-white rounded-2xl p-8 border border-gray-100 text-center">
          <p className="text-[#6B7280]">No events match this category and filter combination.</p>
        </div>
      )}

      {!loading && !error && filteredEvents.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 gap-6">
          {filteredEvents.map((event) => (
            <div
              key={event.id}
              className="bg-white rounded-3xl overflow-hidden border border-gray-100 shadow-md hover:shadow-xl transition-all"
            >
              <div className="h-52 bg-gray-100">
                <img
                  src={event.imageUrl || "https://placehold.co/800x500?text=Event"}
                  alt={event.name}
                  className="w-full h-full object-cover"
                />
              </div>

              <div className="p-5">
                <h3 className="text-xl text-[#1F2937] mb-3">{event.name}</h3>

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

                <div className="flex gap-3 mt-5 flex-wrap">
                  <button
                    onClick={() => setSelectedEvent(event)}
                    className="px-4 py-2 bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB] transition-all"
                  >
                    View More
                  </button>

                  <button
                    onClick={() => {
                      if (!isLoggedIn) { navigate("/login"); return; }
                      if (!isOrganizer) navigate(`/reservation/${event.id}`);
                    }}
                    disabled={isOrganizer}
                    title={
                      !isLoggedIn ? "Login to buy tickets" :
                      isOrganizer ? "Organizers cannot buy tickets" : ""
                    }
                    className={`px-4 py-2 rounded-full transition-all ${
                      isLoggedIn && !isOrganizer
                        ? "bg-[#EC4899] text-white hover:bg-[#DB2777]"
                        : "bg-gray-100 text-gray-400 cursor-not-allowed"
                    }`}
                  >
                    Buy Tickets
                  </button>

                  <button
                    onClick={() => toggleFavorite(event.id)}
                    className={`px-4 py-2 rounded-full transition-all flex items-center gap-2 ${
                      favorites.includes(event.id)
                        ? "bg-red-50 text-red-600"
                        : "bg-gray-100 text-[#1F2937]"
                    }`}
                  >
                    <Heart className="w-4 h-4" />
                    {favorites.includes(event.id) ? "Saved" : "Favorite"}
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {selectedEvent && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-white rounded-3xl max-w-3xl w-full overflow-hidden shadow-2xl">
            <div className="h-72 bg-gray-100">
              <img
                src={selectedEvent.imageUrl || "https://placehold.co/1200x600?text=Event"}
                alt={selectedEvent.name}
                className="w-full h-full object-cover"
              />
            </div>

            <div className="p-6">
              <div className="flex justify-between gap-4 items-start mb-4">
                <div>
                  <h2 className="text-3xl text-[#1F2937]">{selectedEvent.name}</h2>
                  <p className="text-[#6B7280] mt-1">{selectedEvent.category}</p>
                </div>

                <button
                  onClick={() => setSelectedEvent(null)}
                  className="px-4 py-2 bg-gray-100 rounded-full hover:bg-gray-200"
                >
                  Close
                </button>
              </div>

              <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-[#6B7280] mb-5">
                <p><strong className="text-[#1F2937]">Date:</strong> {formatEventDate(selectedEvent)}</p>
                <p><strong className="text-[#1F2937]">Artist:</strong> {selectedEvent.artist || "N/A"}</p>
                <p><strong className="text-[#1F2937]">Organization:</strong> {selectedEvent.organization || "N/A"}</p>
                <p><strong className="text-[#1F2937]">Venue:</strong> {selectedEvent.venue || selectedEvent.location || "N/A"}</p>
                <p><strong className="text-[#1F2937]">City:</strong> {selectedEvent.city || "N/A"}</p>
                <p><strong className="text-[#1F2937]">Country:</strong> {selectedEvent.country || "N/A"}</p>
              </div>

              <div className="mb-6">
                <h3 className="text-xl text-[#1F2937] mb-2">Overview</h3>
                <p className="text-[#6B7280] leading-7">
                  {selectedEvent.overview || "No overview provided for this event yet."}
                </p>
              </div>

              <div className="flex gap-3 flex-wrap">
                <button
                  onClick={() => toggleFavorite(selectedEvent.id)}
                  className={`px-5 py-3 rounded-full ${
                    favorites.includes(selectedEvent.id)
                      ? "bg-red-50 text-red-600"
                      : "bg-gray-100 text-[#1F2937]"
                  }`}
                >
                  {favorites.includes(selectedEvent.id) ? "Remove Favorite" : "Save to Favorite"}
                </button>

                <button
                  onClick={() => {
                    if (!isLoggedIn) { navigate("/login"); return; }
                    if (!isOrganizer) navigate(`/reservation/${selectedEvent.id}`);
                  }}
                  disabled={isOrganizer}
                  title={
                    !isLoggedIn ? "Login to buy tickets" :
                    isOrganizer ? "Organizers cannot buy tickets" : ""
                  }
                  className={`px-5 py-3 rounded-full ${
                    isLoggedIn && !isOrganizer
                      ? "bg-[#EC4899] text-white hover:bg-[#DB2777]"
                      : "bg-gray-100 text-gray-400 cursor-not-allowed"
                  }`}
                >
                  Buy Tickets
                </button>

                <Link
                  to="/"
                  className="px-5 py-3 bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB]"
                >
                  Back Home
                </Link>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}