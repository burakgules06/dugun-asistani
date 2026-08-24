// Pulsing placeholder block for async content — pass height/className to shape it per use site.
import { cn } from "../utils/cn";

export function Skeleton({ className }: { className?: string }) {
  return <div className={cn("rounded-2xl bg-[#f2f2f7] animate-pulse", className)} />;
}
