// Expand/collapse card header — replaces five hand-rolled +/− toggle headers duplicated across
// staff-exceptions and recurring-appointments.
import { useState, type ReactNode } from "react";
import { cn } from "../utils/cn";

interface CollapsibleSectionProps {
  title: ReactNode;
  defaultOpen?: boolean;
  children: ReactNode;
  className?: string;
}

export function CollapsibleSection({ title, defaultOpen = false, children, className }: CollapsibleSectionProps) {
  const [open, setOpen] = useState(defaultOpen);

  return (
    <div className={cn("rounded-2xl border border-[#e8edf5] bg-white overflow-hidden", className)}>
      <button
        type="button"
        onClick={() => setOpen((v) => !v)}
        className="flex w-full items-center justify-between px-4 py-3 text-left text-sm font-semibold text-[#1c1c1e] cursor-pointer"
        style={{ WebkitTapHighlightColor: "transparent" }}
      >
        {title}
        <span className="text-lg leading-none text-[#8e8e93]">{open ? "−" : "+"}</span>
      </button>
      {open && <div className="px-4 pb-4">{children}</div>}
    </div>
  );
}
