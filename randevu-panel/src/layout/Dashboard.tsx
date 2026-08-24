// Composition root for the app shell. Unlike the original appointment panel (a fixed 480px phone-shell
// even on desktop), the sales/kanban screens need real width on desktop — so the shell is a single
// column capped at 480px on mobile, widening up to 1280px on md+ screens, while staying edge-to-edge
// full height on mobile like before.
import type { ReactNode } from "react";
import type { LoginResponse } from "../shared/types";
import { Header } from "./components/Header";
import { SubscriptionBanner } from "./components/SubscriptionBanner";
import type { ViewMode } from "./navigation";

interface DashboardProps {
  auth: LoginResponse;
  isAdmin: boolean;
  viewMode: ViewMode;
  setViewMode: (mode: ViewMode) => void;
  onLogout: () => void;
  children: ReactNode;
}

export function Dashboard({ auth, isAdmin, viewMode, setViewMode, onLogout, children }: DashboardProps) {
  return (
    <div className="w-screen h-dvh bg-[#f4f4f7] flex justify-center items-stretch overflow-hidden overscroll-y-none">
      <div
        className="w-full max-w-[480px] md:max-w-[1280px] h-full flex flex-col box-border bg-white md:bg-transparent shadow-[0_0_24px_rgba(0,0,0,0.04)] md:shadow-none overflow-hidden relative"
        style={{ fontFamily: '-apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif' }}
      >
        <Header tenantName={auth.tenantName} viewMode={viewMode} onNavigate={setViewMode} isAdmin={isAdmin} onLogout={onLogout} />

        {auth.subscriptionUntil && <SubscriptionBanner subscriptionUntil={auth.subscriptionUntil} />}

        <main
          className="flex-1 overflow-y-auto px-5 pb-5 pt-3 box-border md:px-8 md:pb-8 bg-white md:rounded-3xl md:my-3 md:shadow-sm md:border md:border-[#eceef2]"
          style={{ WebkitOverflowScrolling: "touch" }}
        >
          {children}
        </main>
      </div>
    </div>
  );
}
