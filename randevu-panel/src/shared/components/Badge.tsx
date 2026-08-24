// Small tone-driven pill: status labels, price/duration chips, and ownership tags all render through
// this instead of each screen hand-rolling its own bg/text/border combination.
import type { ReactNode } from "react";
import { cn } from "../utils/cn";
import { TONES, type Tone } from "../theme/tones";

interface BadgeProps {
  tone?: Tone;
  children: ReactNode;
  className?: string;
}

export function Badge({ tone = "gray", children, className }: BadgeProps) {
  const t = TONES[tone];
  return (
    <span className={cn("inline-flex items-center gap-1 text-[11px] font-semibold px-2 py-0.5 rounded-md border", t.soft, t.softText, t.softBorder, className)}>
      {children}
    </span>
  );
}
