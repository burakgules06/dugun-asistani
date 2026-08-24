import { apiFetch } from "./client";
import type { Hall } from "../shared/types";

export function getHalls(includeInactive = false): Promise<Hall[]> {
  return apiFetch(`/api/hall?includeInactive=${includeInactive}`);
}

export type HallUpsert = {
  name: string;
  description?: string | null;
  capacityMin?: number | null;
  capacityMax?: number | null;
  dailyCapacity?: number;
};

export function createHall(data: HallUpsert): Promise<Hall> {
  return apiFetch("/api/hall/create", { method: "POST", body: JSON.stringify(data) });
}

export function updateHall(id: string, data: HallUpsert): Promise<Hall> {
  return apiFetch(`/api/hall/update/${id}`, { method: "PUT", body: JSON.stringify(data) });
}

export function setHallActive(id: string, active: boolean): Promise<Hall> {
  return apiFetch(`/api/hall/set-active/${id}`, { method: "PATCH", body: JSON.stringify({ active }) });
}
