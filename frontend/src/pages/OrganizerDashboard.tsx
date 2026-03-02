import { Link } from "react-router-dom";
import { TicketPlus } from "lucide-react";

export default function OrganizerDashboard() {
  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
      <div className="bg-white rounded-3xl shadow-lg p-8 border border-gray-200">
        <div className="flex items-center gap-4 mb-6">
          <div className="bg-[#3B82F6] p-4 rounded-2xl shadow-lg">
            <TicketPlus className="w-10 h-10 text-white" />
          </div>
          <div>
            <h1 className="text-4xl text-[#1F2937]">Organizer Dashboard</h1>
            <p className="text-[#6B7280] mt-1">
              Create and manage events.
            </p>
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
      </div>
    </div>
  );
}