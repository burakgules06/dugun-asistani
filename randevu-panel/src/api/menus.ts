import { apiFetch } from "./client";
import type { Menu } from "../shared/types";

export function getMenus(includeInactive = false): Promise<Menu[]> {
  return apiFetch(`/api/menu?includeInactive=${includeInactive}`);
}

export function createMenu(data: {
  name: string;
  description?: string | null;
  pricePerPerson?: number | null;
  hallIds?: string[];
}): Promise<Menu> {
  return apiFetch("/api/menu/create", { method: "POST", body: JSON.stringify(data) });
}

export function updateMenu(id: string, data: {
  name: string;
  description?: string | null;
  pricePerPerson?: number | null;
  hallIds?: string[];
}): Promise<Menu> {
  return apiFetch(`/api/menu/update/${id}`, { method: "PUT", body: JSON.stringify(data) });
}

export function setMenuActive(id: string, active: boolean): Promise<Menu> {
  return apiFetch(`/api/menu/set-active/${id}`, { method: "PATCH", body: JSON.stringify({ active }) });
}
