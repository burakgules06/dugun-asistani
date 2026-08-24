import { apiFetch } from "./client";
import type { LoginResponse } from "../shared/types";

export function login(phone: string, password: string): Promise<LoginResponse> {
  return apiFetch<LoginResponse>("/api/auth/login", {
    method: "POST",
    body: JSON.stringify({ phone, password }),
  });
}
