// Icon + title + count label above a grouped list — generalized from services' local "Section".
import type { ReactNode } from "react";

interface SectionHeaderProps {
  title: string;
  count?: number;
  icon?: ReactNode;
}

export function SectionHeader({ title, count, icon }: SectionHeaderProps) {
  return (
    <div className="flex items-center gap-2 px-1">
      {icon}
      <h3 className="m-0 text-[13px] font-bold text-[#48484a] tracking-wide uppercase">{title}</h3>
      {count !== undefined && (
        <span className="text-[11px] font-bold text-[#8e8e93] bg-[#f2f2f7] rounded-full px-2 py-0.5">{count}</span>
      )}
    </div>
  );
}
