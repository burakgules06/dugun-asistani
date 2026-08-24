import { apiFetch } from "./client";
import type { PanelUser } from "../shared/types";

export function getUsers(): Promise<PanelUser[]> {
  return apiFetch("/api/user");
}

export function createUser(data: {
  phone: string;
  password: string;
  fullName?: string | null;
  role: "ADMIN" | "STAFF";
}): Promise<PanelUser> {
  return apiFetch("/api/user/create", { method: "POST", body: JSON.stringify(data) });
}

export function setUserActive(id: string, active: boolean): Promise<PanelUser> {
  return apiFetch(`/api/user/set-active/${id}`, { method: "PATCH", body: JSON.stringify({ active }) });
}

export function changePassword(currentPassword: string, newPassword: string): Promise<void> {
  return apiFetch("/api/user/change-password", {
    method: "POST",
    body: JSON.stringify({ currentPassword, newPassword }),
  });
}
