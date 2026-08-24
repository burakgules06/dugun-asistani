import type { Lead, LeadStage } from "../../shared/types";

export const STAGES: LeadStage[] = ["NEW", "CONTACTED", "PRICE_GIVEN", "INVITED", "WON", "LOST"];
export const ACTIVE_STAGES: LeadStage[] = ["NEW", "CONTACTED", "PRICE_GIVEN", "INVITED"];
export const RESOLVED_STAGES: LeadStage[] = ["WON", "LOST"];

function startOfDay(d: Date): Date {
  const c = new Date(d);
  c.setHours(0, 0, 0, 0);
  return c;
}

export function isToday(isoDateTime: string): boolean {
  const d = new Date(isoDateTime);
  const today = startOfDay(new Date());
  return startOfDay(d).getTime() === today.getTime();
}

export interface LeadFilters {
  hallId: string;
  mood: string;
}

export function applyFilters(leads: Lead[], filters: LeadFilters): Lead[] {
  return leads.filter((lead) => {
    if (filters.hallId && lead.hallId !== filters.hallId) return false;
    if (filters.mood && lead.mood !== filters.mood) return false;
    return true;
  });
}

export function groupByStage(leads: Lead[]): Record<LeadStage, Lead[]> {
  const groups: Record<LeadStage, Lead[]> = { NEW: [], CONTACTED: [], PRICE_GIVEN: [], INVITED: [], WON: [], LOST: [] };
  for (const lead of leads) {
    groups[lead.stage].push(lead);
  }
  return groups;
}

function dateKey(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

export function mondayOf(d: Date): Date {
  const c = startOfDay(d);
  const dow = (c.getDay() + 6) % 7; // Pazartesi = 0
  c.setDate(c.getDate() - dow);
  return c;
}

/** Bugünün haftasına göre kaç hafta öncesine ait (0 = bu hafta, 1 = geçen hafta, ...). */
export function weeksAgo(mondayDate: Date): number {
  const thisMonday = mondayOf(new Date());
  const diffDays = Math.round((thisMonday.getTime() - mondayDate.getTime()) / 86400000);
  return Math.round(diffDays / 7);
}

export interface WeekGroup {
  key: string;
  mondayDate: Date;
  leads: Lead[];
}

export function groupByWeek(leads: Lead[]): WeekGroup[] {
  const map = new Map<string, WeekGroup>();
  for (const lead of leads) {
    const monday = mondayOf(new Date(lead.createdAt));
    const key = dateKey(monday);
    let group = map.get(key);
    if (!group) {
      group = { key, mondayDate: monday, leads: [] };
      map.set(key, group);
    }
    group.leads.push(lead);
  }
  return Array.from(map.values()).sort((a, b) => b.mondayDate.getTime() - a.mondayDate.getTime());
}

export function groupByStageAndWeek(leads: Lead[], stages: LeadStage[] = ACTIVE_STAGES): Record<LeadStage, WeekGroup[]> {
  const byStage = groupByStage(leads);
  const result = {} as Record<LeadStage, WeekGroup[]>;
  for (const stage of stages) {
    result[stage] = groupByWeek(byStage[stage]);
  }
  return result;
}

export type ResolvedSort = "day_desc" | "day_asc" | "guests_desc";

export interface ResolvedFilters {
  stage: "" | "WON" | "LOST";
  hallId: string;
  search: string;
  sort: ResolvedSort;
}

export function applyResolvedFilters(leads: Lead[], filters: ResolvedFilters): Lead[] {
  const q = filters.search.trim().toLowerCase();
  const result = leads.filter((lead) => {
    if (filters.stage && lead.stage !== filters.stage) return false;
    if (filters.hallId && lead.hallId !== filters.hallId) return false;
    if (q && !lead.customerName.toLowerCase().includes(q) && !(lead.customerPhone ?? "").toLowerCase().includes(q)) return false;
    return true;
  });

  result.sort((a, b) => {
    if (filters.sort === "guests_desc") return (b.guestCountMin ?? 0) - (a.guestCountMin ?? 0);
    const diff = new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime();
    return filters.sort === "day_asc" ? -diff : diff;
  });

  return result;
}
