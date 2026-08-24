import { apiFetch } from "./client";
import type { CapacityRule, TimeSlot } from "../shared/types";

export type CapacityRuleUpsert = {
  hallId?: string | null;
  menuId?: string | null;
  months?: number[] | null;
  timeSlot?: TimeSlot | null;
  active?: boolean;
  note?: string | null;
};

export function getCapacityRules(): Promise<CapacityRule[]> {
  return apiFetch("/api/capacity-rule");
}

export function createCapacityRule(data: CapacityRuleUpsert): Promise<CapacityRule> {
  return apiFetch("/api/capacity-rule/create", { method: "POST", body: JSON.stringify(data) });
}

export function updateCapacityRule(id: string, data: CapacityRuleUpsert): Promise<CapacityRule> {
  return apiFetch(`/api/capacity-rule/update/${id}`, { method: "PUT", body: JSON.stringify(data) });
}

export function deleteCapacityRule(id: string): Promise<void> {
  return apiFetch(`/api/capacity-rule/${id}`, { method: "DELETE" });
}
