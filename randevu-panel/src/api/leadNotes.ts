import { apiFetch } from "./client";
import type { LeadNote } from "../shared/types";

export function getLeadNotes(leadId: string): Promise<LeadNote[]> {
  return apiFetch(`/api/lead/${leadId}/note`);
}

export function addLeadNote(leadId: string, body: string): Promise<LeadNote> {
  return apiFetch(`/api/lead/${leadId}/note`, { method: "POST", body: JSON.stringify({ body }) });
}
