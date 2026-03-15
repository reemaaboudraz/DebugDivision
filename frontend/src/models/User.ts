export const Role =  {
    organizer: "ORGANIZER",
    customer: "CUSTOMER"
} as const;

export type Role = (typeof Role)[keyof typeof Role]

export interface UserProfile {
    name: string,
    role: Role,
    email?: string,
    phone?: string
};