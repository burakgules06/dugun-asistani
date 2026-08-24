import { useEffect, useMemo, useState } from "react";
import { createLead, getLeads, updateLead, type LeadUpdate } from "../../api/leads";
import { getHalls } from "../../api/halls";
import { getMenus } from "../../api/menus";
import { useResolveError } from "../../shared/errors";
import type { Hall, Lead, Menu } from "../../shared/types";
import { buildMenuColorMap } from "./calendarColors";

function toDateKey(d: Date): string {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, "0");
  const day = String(d.getDate()).padStart(2, "0");
  return `${y}-${m}-${day}`;
}

/** Pazartesi başlangıçlı, hedef ayı tam kapsayan hafta bazlı takvim gridi üretir. */
function buildMonthGrid(monthStart: Date): Date[] {
  const firstOfMonth = new Date(monthStart.getFullYear(), monthStart.getMonth(), 1);
  const firstWeekday = (firstOfMonth.getDay() + 6) % 7; // Pazartesi = 0
  const gridStart = new Date(firstOfMonth);
  gridStart.setDate(firstOfMonth.getDate() - firstWeekday);

  const lastOfMonth = new Date(monthStart.getFullYear(), monthStart.getMonth() + 1, 0);
  const lastWeekday = (lastOfMonth.getDay() + 6) % 7;
  const gridEnd = new Date(lastOfMonth);
  gridEnd.setDate(lastOfMonth.getDate() + (6 - lastWeekday));

  const days: Date[] = [];
  const cursor = new Date(gridStart);
  while (cursor.getTime() <= gridEnd.getTime()) {
    days.push(new Date(cursor));
    cursor.setDate(cursor.getDate() + 1);
  }
  return days;
}

export function useCalendar() {
  const resolve = useResolveError();
  const [leads, setLeads] = useState<Lead[] | null>(null);
  const [halls, setHalls] = useState<Hall[]>([]);
  const [menus, setMenus] = useState<Menu[]>([]);
  const [error, setError] = useState("");
  const [monthCursor, setMonthCursor] = useState(() => {
    const now = new Date();
    return new Date(now.getFullYear(), now.getMonth(), 1);
  });

  useEffect(() => {
    getLeads().then(setLeads).catch((e) => setError(resolve(e)));
    getHalls(true).then(setHalls).catch(() => setHalls([]));
    getMenus(true).then(setMenus).catch(() => setMenus([]));
  }, [resolve]);

  const menuColors = useMemo(() => buildMenuColorMap(menus), [menus]);

  const eventLeads = useMemo(() => (leads ?? []).filter((l) => l.eventDate), [leads]);

  const leadsByDate = useMemo(() => {
    const map = new Map<string, Lead[]>();
    for (const lead of eventLeads) {
      const key = lead.eventDate as string;
      const arr = map.get(key) ?? [];
      arr.push(lead);
      map.set(key, arr);
    }
    return map;
  }, [eventLeads]);

  const gridDays = useMemo(() => buildMonthGrid(monthCursor), [monthCursor]);

  function goToPrevMonth() {
    setMonthCursor((prev) => new Date(prev.getFullYear(), prev.getMonth() - 1, 1));
  }
  function goToNextMonth() {
    setMonthCursor((prev) => new Date(prev.getFullYear(), prev.getMonth() + 1, 1));
  }
  function goToToday() {
    const now = new Date();
    setMonthCursor(new Date(now.getFullYear(), now.getMonth(), 1));
  }
  function goToMonth(year: number, month: number) {
    setMonthCursor(new Date(year, month, 1));
  }

  async function saveLead(leadId: string, data: LeadUpdate) {
    const updated = await updateLead(leadId, data);
    setLeads((cur) => cur?.map((l) => (l.id === leadId ? updated : l)) ?? null);
    return updated;
  }

  async function addLead(data: Parameters<typeof createLead>[0]) {
    const created = await createLead(data);
    setLeads((cur) => (cur ? [created, ...cur] : [created]));
    return created;
  }

  return {
    leads,
    saveLead,
    addLead,
    halls,
    menus,
    menuColors,
    error,
    monthCursor,
    gridDays,
    leadsByDate,
    toDateKey,
    goToPrevMonth,
    goToNextMonth,
    goToToday,
    goToMonth,
  };
}
