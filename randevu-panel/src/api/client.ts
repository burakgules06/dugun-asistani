const API_BASE = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";
const TOKEN_KEY = "venuecrm_token";
const AUTH_KEY = "venuecrm_auth";

import { ApiError } from "../shared/errors/ApiError";
import type { LoginResponse } from "../shared/types";

let authToken: string | null = localStorage.getItem(TOKEN_KEY);
let onSessionExpired: (() => void) | null = null;

export function setSessionExpiredHandler(handler: (() => void) | null) {
  onSessionExpired = handler;
}

export function setAuthToken(token: string | null) {
  authToken = token;
  if (token) {
    localStorage.setItem(TOKEN_KEY, token);
  } else {
    localStorage.removeItem(TOKEN_KEY);
  }
}

export function getAuthToken(): string | null {
  return authToken;
}

export function saveAuth(auth: LoginResponse) {
  setAuthToken(auth.token);
  localStorage.setItem(AUTH_KEY, JSON.stringify(auth));
}

export function loadAuth(): LoginResponse | null {
  const raw = localStorage.getItem(AUTH_KEY);
  if (!raw) return null;
  try {
    const auth = JSON.parse(raw) as LoginResponse;
    setAuthToken(auth.token);
    return auth;
  } catch {
    return null;
  }
}

export function clearAuth() {
  setAuthToken(null);
  localStorage.removeItem(AUTH_KEY);
}

export async function apiFetch<T>(
  path: string,
  options: RequestInit = {}
): Promise<T> {
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };
  if (authToken) {
    headers["Authorization"] = `Bearer ${authToken}`;
  }

  const res = await fetch(`${API_BASE}${path}`, { ...options, headers });

  if (res.status === 401) {
    if (path.includes("/auth/login")) {
      const text = await res.text().catch(() => "");
      try {
        const json = JSON.parse(text);
        throw new ApiError(401, json.error ?? "Telefon veya şifre hatalı");
      } catch (e) {
        if (e instanceof ApiError) throw e;
        throw new ApiError(401, "Telefon veya şifre hatalı");
      }
    }
    clearAuth();
    onSessionExpired?.();
    throw new ApiError(401, "Oturum süresi doldu");
  }

  if (res.status === 403) {
    throw new ApiError(403, "Bu işlem için yetkiniz yok");
  }

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    try {
      const json = JSON.parse(text);
      if (json.success === false && typeof json.code === "number") {
        throw new ApiError(json.code, json.message ?? text);
      }
    } catch (parseErr) {
      if (parseErr instanceof ApiError) throw parseErr;
    }
    throw new Error(text || `İstek başarısız (${res.status})`);
  }

  const contentType = res.headers.get("content-type");
  if (contentType && contentType.includes("application/json")) {
    const json = await res.json();
    if (json && json.success === false && typeof json.code === "number") {
      throw new ApiError(json.code, json.message ?? "");
    }
    return json as T;
  }
  return undefined as T;
}
