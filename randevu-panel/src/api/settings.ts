import { apiFetch } from "./client";
import type { TenantSettings } from "../shared/types";

export function getSettings(): Promise<TenantSettings> {
  return apiFetch("/api/settings");
}

export function updateSettings(data: {
  displayName?: string;
  greetingText?: string | null;
  showPrices?: boolean;
  waPhoneNumberId?: string;
  waDisplayNumber?: string;
  waAccessToken?: string;
}): Promise<TenantSettings> {
  return apiFetch("/api/settings", { method: "PUT", body: JSON.stringify(data) });
}
