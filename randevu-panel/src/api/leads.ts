import { apiFetch } from "./client";
import type { Lead, LeadMood, LeadStage, TimeSlot } from "../shared/types";

export function getLeads(): Promise<Lead[]> {
  return apiFetch("/api/lead");
}

export function getLead(id: string): Promise<Lead> {
  return apiFetch(`/api/lead/${id}`);
}

export function createLead(data: {
  customerName: string;
  customerPhone?: string | null;
  hallId?: string | null;
  menuId?: string | null;
  eventDate?: string | null;
  guestCountMin?: number | null;
  guestCountMax?: number | null;
  preferredTimeSlot?: TimeSlot | "" | null;
}): Promise<Lead> {
  return apiFetch("/api/lead/create", { method: "POST", body: JSON.stringify(data) });
}

export type LeadUpdate = Partial<{
  hallId: string | null;
  menuId: string | null;
  eventDate: string | null;
  guestCountMin: number | null;
  guestCountMax: number | null;
  preferredTimeSlot: TimeSlot | "" | null;
  stage: LeadStage;
  mood: LeadMood | "";
  priceGiven: boolean;
  priceAmount: number | null;
  assignedUserId: string | null;
}>;

export function updateLead(id: string, data: LeadUpdate): Promise<Lead> {
  return apiFetch(`/api/lead/update/${id}`, { method: "PUT", body: JSON.stringify(data) });
}
