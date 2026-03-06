export type TEvent = {
  // i called this one TEvent and not just Event because of name conflict
  id: string;
  name: string;
  location: string;
  availableTickets: number;
  eventDate: { seconds: number; nanos: number }; // thats how firebase has timestamp, not just a string
  organizerId: string;
};

export type CreateEventRequest = {
  name: string;
  location: string;
  availableTickets: number;
  eventDateMillis: number; // date-only stored as noon UTC
  organizerId?: string; // temporary until auth
};

export type CreateEventResponse = {
  id: string;
};
