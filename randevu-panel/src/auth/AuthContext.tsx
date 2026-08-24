// Centralizes session state so features can read `auth`/call `logout()` without prop-drilling
// through App.tsx. Session bootstrap still lives here: loads any saved token on first render,
// and wires apiFetch's 401 handler back to clearing the session.
import { createContext, useContext, useEffect, useState, type ReactNode } from "react";
import { clearAuth, loadAuth, saveAuth, setSessionExpiredHandler } from "../api/client";
import type { LoginResponse } from "../shared/types";

interface AuthContextValue {
  auth: LoginResponse | null;
  isAdmin: boolean;
  isSuperAdmin: boolean;
  login: (data: LoginResponse) => void;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [auth, setAuth] = useState<LoginResponse | null>(() => loadAuth());

  useEffect(() => {
    setSessionExpiredHandler(() => setAuth(null));
    return () => setSessionExpiredHandler(null);
  }, []);

  function login(data: LoginResponse) {
    saveAuth(data);
    setAuth(data);
  }

  function logout() {
    clearAuth();
    setAuth(null);
  }

  const value: AuthContextValue = {
    auth,
    isAdmin: auth?.role === "ADMIN",
    isSuperAdmin: auth?.role === "SUPER_ADMIN",
    login,
    logout,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used inside AuthProvider");
  return ctx;
}
