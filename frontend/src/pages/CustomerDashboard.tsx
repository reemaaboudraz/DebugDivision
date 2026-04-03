import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Calendar, Ticket, XCircle, AlertTriangle, Loader2, PartyPopper, Clock } from "lucide-react";
import { useAuth } from "@/AuthContext";
import { getReservationsByUser, cancelReservation } from "@/services/reservations";
import type { ReservationResponse } from "@/services/reservations";
import Profile from "@/components/auth/Profile";

function formatDate(seconds: number) {
  return new Date(seconds * 1000).toLocaleString(undefined, {
    dateStyle: "medium",
    timeStyle: "short",
  });
}

function StatusBadge({ status }: { status: string }) {
  const s = status.toUpperCase();
  const styles: Record<string, string> = {
    CONFIRMED: "bg-emerald-50 text-emerald-700 border border-emerald-200",
    CANCELLED: "bg-gray-100 text-gray-400 border border-gray-200",
  };
  return (
    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${styles[s] ?? "bg-gray-100 text-gray-500 border border-gray-200"}`}>
      {s}
    </span>
  );
}

function CancelModal({
  reservation, onConfirm, onClose, loading, error,
}: {
  reservation: ReservationResponse;
  onConfirm: () => void;
  onClose: () => void;
  loading: boolean;
  error: string | null;
}) {
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-sm">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6 space-y-4">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-red-50 flex items-center justify-center flex-shrink-0">
            <AlertTriangle className="w-5 h-5 text-red-500" />
          </div>
          <div>
            <h3 className="text-lg font-semibold text-[#1F2937]">Cancel Reservation</h3>
            <p className="text-sm text-[#6B7280]">This action cannot be undone.</p>
          </div>
        </div>
        <p className="text-sm text-[#374151]">
          Cancel <strong>{reservation.numberOfTickets} ticket(s)</strong> for{" "}
          <strong>{reservation.eventName}</strong>? Tickets will be returned to the pool.
        </p>
        {error && (
          <div className="bg-red-50 border border-red-100 text-red-700 text-sm rounded-xl px-4 py-3">
            {error}
          </div>
        )}
        <div className="flex gap-3 pt-1">
          <button
            onClick={onClose}
            disabled={loading}
            className="flex-1 px-4 py-2.5 rounded-xl border border-gray-200 text-[#374151] text-sm font-medium hover:bg-gray-50 disabled:opacity-50"
          >
            Keep It
          </button>
          <button
            onClick={onConfirm}
            disabled={loading}
            className="flex-1 px-4 py-2.5 rounded-xl bg-red-500 text-white text-sm font-medium hover:bg-red-600 disabled:opacity-60 flex items-center justify-center gap-2"
          >
            {loading ? <><Loader2 className="w-4 h-4 animate-spin" />Cancelling…</> : "Yes, Cancel"}
          </button>
        </div>
      </div>
    </div>
  );
}

function ReservationCard({
  reservation, onCancelRequest,
}: {
  reservation: ReservationResponse;
  onCancelRequest: (r: ReservationResponse) => void;
}) {
  const isCancelled = reservation.status.toUpperCase() === "CANCELLED";
  return (
    <div className={`bg-white rounded-2xl border shadow-sm overflow-hidden transition-all ${isCancelled ? "border-gray-100 opacity-60" : "border-gray-100 hover:shadow-md"}`}>
      <div className="p-5 flex flex-col gap-3">
        <div className="flex items-start justify-between gap-2">
          <h3 className="text-base font-semibold text-[#1F2937] leading-tight">{reservation.eventName}</h3>
          <StatusBadge status={reservation.status} />
        </div>

        <div className="space-y-1.5 text-sm">
          <div className="flex items-center gap-2 text-[#1F2937]">
            <Calendar className="w-4 h-4 flex-shrink-0 text-[#3B82F6]" />
            <span>
              <span className="font-medium">Event: </span>
              {reservation.eventDate ? formatDate(reservation.eventDate.seconds) : "Date not available"}
            </span>
          </div>
          <div className="flex items-center gap-2 text-[#6B7280]">
            <Clock className="w-4 h-4 flex-shrink-0" />
            <span>
              <span className="font-medium">Booked: </span>
              {formatDate(reservation.createdAt.seconds)}
            </span>
          </div>
          <div className="flex items-center gap-2 text-[#6B7280]">
            <Ticket className="w-4 h-4 flex-shrink-0" />
            <span>{reservation.numberOfTickets} ticket{reservation.numberOfTickets !== 1 ? "s" : ""}</span>
          </div>
        </div>

        <p className="text-xs text-[#9CA3AF]">ID: <span className="font-mono">{reservation.id}</span></p>

        {isCancelled && reservation.cancelReason === "EVENT_CANCELLED" && (
          <p className="text-xs text-amber-600 font-medium">This event was cancelled by the organizer.</p>
        )}

        {!isCancelled && (
          <button
            onClick={() => onCancelRequest(reservation)}
            className="mt-1 w-full flex items-center justify-center gap-1.5 px-4 py-2 rounded-xl border border-red-100 text-red-500 text-sm font-medium hover:bg-red-50 hover:border-red-200 transition-all"
          >
            <XCircle className="w-4 h-4" />
            Cancel Reservation
          </button>
        )}
      </div>
    </div>
  );
}

export default function CustomerDashboard() {
  const navigate = useNavigate();
  const { uid, userProfile } = useAuth();

  const [reservations, setReservations] = useState<ReservationResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [fetchError, setFetchError] = useState<string | null>(null);
  const [cancelTarget, setCancelTarget] = useState<ReservationResponse | null>(null);
  const [cancelling, setCancelling] = useState(false);
  const [cancelError, setCancelError] = useState<string | null>(null);

  useEffect(() => {
    if (!uid) { navigate("/login"); return; }
    if (!userProfile?.email) {
      setFetchError("Unable to load reservations: account email not found.");
      setLoading(false);
      return;
    }
    getReservationsByUser(userProfile.email)
      .then(setReservations)
      .catch((err) => setFetchError(err?.message ?? "Failed to load reservations."))
      .finally(() => setLoading(false));
  }, [uid, userProfile?.email, navigate]);

  async function handleConfirmCancel() {
    if (!cancelTarget) return;
    setCancelling(true);
    setCancelError(null);
    try {
      const updated = await cancelReservation(cancelTarget.id);
      setReservations((prev) => prev.map((r) => (r.id === updated.id ? updated : r)));
      setCancelTarget(null);
    } catch (err: unknown) {
      setCancelError(err instanceof Error ? err.message : "Cancellation failed.");
    } finally {
      setCancelling(false);
    }
  }

  const active = reservations.filter((r) => r.status.toUpperCase() !== "CANCELLED");
  const cancelled = reservations.filter((r) => r.status.toUpperCase() === "CANCELLED");

  return (
    <div className="max-w-4xl mx-auto px-4 py-10 space-y-8">
      <Profile />

      <section>
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-2xl font-semibold text-[#1F2937]">My Tickets</h2>
          <button
            onClick={() => navigate("/events")}
            className="text-sm px-4 py-2 bg-[#3B82F6] text-white rounded-full hover:bg-[#2563EB] transition-all"
          >
            Browse Events
          </button>
        </div>

        {loading && (
          <div className="flex items-center gap-2 text-[#6B7280] py-10 justify-center">
            <Loader2 className="w-5 h-5 animate-spin" />
            Loading your tickets…
          </div>
        )}

        {fetchError && (
          <div className="bg-red-50 border border-red-100 text-red-700 rounded-xl p-4 text-sm">
            {fetchError}
          </div>
        )}

        {!loading && !fetchError && reservations.length === 0 && (
          <div className="text-center py-16 text-[#6B7280] space-y-3">
            <PartyPopper className="w-10 h-10 mx-auto text-[#D1D5DB]" />
            <p className="text-base">No reservations yet.</p>
            <button onClick={() => navigate("/events")} className="text-sm text-[#3B82F6] hover:underline">
              Find an event to attend →
            </button>
          </div>
        )}

        {!loading && active.length > 0 && (
          <div className="space-y-3">
            <h3 className="text-sm font-medium text-[#6B7280] uppercase tracking-wide">Active ({active.length})</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {active.map((r) => (
                <ReservationCard key={r.id} reservation={r} onCancelRequest={setCancelTarget} />
              ))}
            </div>
          </div>
        )}

        {!loading && cancelled.length > 0 && (
          <div className="space-y-3 mt-8">
            <h3 className="text-sm font-medium text-[#6B7280] uppercase tracking-wide">Cancelled ({cancelled.length})</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
              {cancelled.map((r) => (
                <ReservationCard key={r.id} reservation={r} onCancelRequest={setCancelTarget} />
              ))}
            </div>
          </div>
        )}
      </section>

      {cancelTarget && (
        <CancelModal
          reservation={cancelTarget}
          onConfirm={handleConfirmCancel}
          onClose={() => { setCancelTarget(null); setCancelError(null); }}
          loading={cancelling}
          error={cancelError}
        />
      )}
    </div>
  );
}