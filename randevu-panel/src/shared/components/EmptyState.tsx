// Centered icon + title + subtitle block for "nothing here yet" screens.
import type { ReactNode } from "react";

interface EmptyStateProps {
  icon?: ReactNode;
  title: string;
  subtitle?: string;
}

export function EmptyState({ icon, title, subtitle }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
      {icon && <div className="w-14 h-14 rounded-2xl bg-[#f2f2f7] flex items-center justify-center mb-3">{icon}</div>}
      <p className="text-sm font-semibold text-[#1c1c1e]">{title}</p>
      {subtitle && <p className="text-xs text-[#8e8e93] font-medium mt-1 max-w-[220px]">{subtitle}</p>}
    </div>
  );
}
