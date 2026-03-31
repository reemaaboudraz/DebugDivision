import emailjs from "@emailjs/browser";

type ReservationLike = {
  id: string;
  userName: string;
  userEmail: string;
  eventName: string;
  eventDate: any;
  status?: string;
};

const SERVICE_ID = import.meta.env.VITE_EMAILJS_SERVICE_ID;
const PUBLIC_KEY = import.meta.env.VITE_EMAILJS_PUBLIC_KEY;
const RESERVATION_TEMPLATE_ID =
  import.meta.env.VITE_EMAILJS_RESERVATION_TEMPLATE_ID;
const CANCELLATION_TEMPLATE_ID =
  import.meta.env.VITE_EMAILJS_CANCELLATION_TEMPLATE_ID;

console.log("EmailJS ENV:", { SERVICE_ID, PUBLIC_KEY, RESERVATION_TEMPLATE_ID, CANCELLATION_TEMPLATE_ID });

function formatEventDate(eventDate: any): string {
  if (!eventDate) return "";

  if (typeof eventDate?.toDate === "function") {
    return eventDate.toDate().toLocaleString();
  }

  if (eventDate?.seconds) {
    return new Date(eventDate.seconds * 1000).toLocaleString();
  }

  if (eventDate?._seconds) {
    return new Date(eventDate._seconds * 1000).toLocaleString();
  }

  return new Date(eventDate).toLocaleString();
}

function validateConfig() {
  if (
    !SERVICE_ID ||
    !PUBLIC_KEY ||
    !RESERVATION_TEMPLATE_ID ||
    !CANCELLATION_TEMPLATE_ID
  ) {
    throw new Error("Missing EmailJS environment variables.");
  }
}

function buildTemplateParams(reservation: ReservationLike) {
  return {
    user_name: reservation.userName,
    user_email: reservation.userEmail,
    event_name: reservation.eventName,
    event_date: formatEventDate(reservation.eventDate),
    reservation_id: reservation.id,
    reservation_status: reservation.status ?? "",
  };
}

export async function sendReservationConfirmationEmail(
  reservation: ReservationLike
) {
  validateConfig();

  return emailjs.send(
    SERVICE_ID,
    RESERVATION_TEMPLATE_ID,
    buildTemplateParams(reservation),
    PUBLIC_KEY
  );
}

export async function sendCancellationConfirmationEmail(
  reservation: ReservationLike
) {
  validateConfig();

  return emailjs.send(
    SERVICE_ID,
    CANCELLATION_TEMPLATE_ID,
    buildTemplateParams(reservation),
    PUBLIC_KEY
  );
}