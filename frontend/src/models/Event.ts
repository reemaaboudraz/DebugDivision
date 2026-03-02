export type CreateEventRequest = {
  name: string;
  location: string;
  availableTickets: number;
  eventDateMillis: number;   // date-only stored as noon UTC
  organizerId?: string;      // temporary until auth
};

export type CreateEventResponse = {
  id: string;
};