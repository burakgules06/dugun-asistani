import { useCallback, useEffect, useMemo, useState } from "react";
import { createLead, getLeads, updateLead, type LeadUpdate } from "../../api/leads";
import { useResolveError } from "../../shared/errors";
import type { Lead, LeadStage } from "../../shared/types";
import { ACTIVE_STAGES, applyFilters, groupByStageAndWeek, RESOLVED_STAGES, type LeadFilters } from "./leadFilters";

export function useLeadBoard() {
  const resolve = useResolveError();
  const [leads, setLeads] = useState<Lead[] | null>(null);
  const [error, setError] = useState("");
  const [filters, setFilters] = useState<LeadFilters>({ hallId: "", mood: "" });

  const load = useCallback(() => {
    getLeads()
      .then(setLeads)
      .catch((e) => setError(resolve(e)));
  }, [resolve]);

  useEffect(load, [load]);

  const activeLeads = useMemo(() => (leads ?? []).filter((l) => ACTIVE_STAGES.includes(l.stage)), [leads]);
  const resolvedLeads = useMemo(() => (leads ?? []).filter((l) => RESOLVED_STAGES.includes(l.stage)), [leads]);
  const filtered = useMemo(() => applyFilters(activeLeads, filters), [activeLeads, filters]);
  const columns = useMemo(() => groupByStageAndWeek(filtered), [filtered]);

  async function moveToStage(leadId: string, stage: LeadStage) {
    const prev = leads;
    setLeads((cur) => cur?.map((l) => (l.id === leadId ? { ...l, stage } : l)) ?? null);
    try {
      await updateLead(leadId, { stage });
    } catch (e) {
      setLeads(prev ?? null);
      setError(resolve(e));
    }
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

  return { leads, columns, filtered, resolvedLeads, error, filters, setFilters, moveToStage, saveLead, addLead, load };
}
