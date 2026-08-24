import { apiFetch } from "./client";
import type { TenantSettings } from "../shared/types";

/** SUPER_ADMIN: tüm işletmeleri listeler. */
export function getAllTenants(): Promise<TenantSettings[]> {
  return apiFetch("/api/admin/tenant");
}
