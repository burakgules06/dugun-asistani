// Success/error/warning/info alert: the single display surface for messages resolved via
// useResolveError() or local validation, replacing ~10 ad hoc banner divs across forms/panels.
import type { ReactNode } from "react";
import { cn } from "../utils/cn";
import { CheckCircleIcon, InfoIcon, WarningIcon } from "../icons";

export type BannerVariant = "success" | "error" | "warning" | "info";

const VARIANT_CLASSES: Record<BannerVariant, string> = {
  success: "bg-[#e6f9f0] text-[#10b981] border-[#c8f0dd]",
  error: "bg-[#fff4f4] text-[#c2410c] border-[#ffd0d0]",
  warning: "bg-[#fff8e1] text-[#92400e] border-[#fde68a]",
  info: "bg-[#eff6ff] text-[#1d4ed8] border-[#dbeafe]",
};

const VARIANT_ICON: Record<BannerVariant, ReactNode> = {
  success: <CheckCircleIcon className="h-4 w-4 shrink-0" />,
  error: <WarningIcon className="h-4 w-4 shrink-0 text-[#c2410c]" />,
  warning: <WarningIcon className="h-4 w-4 shrink-0 text-[#92400e]" />,
  info: <InfoIcon className="h-4 w-4 shrink-0" />,
};

interface BannerProps {
  variant: BannerVariant;
  children: ReactNode;
  className?: string;
  icon?: boolean;
}

export function Banner({ variant, children, className, icon = true }: BannerProps) {
  return (
    <div className={cn("flex items-start gap-2 rounded-xl border px-3 py-2.5 text-xs font-medium", VARIANT_CLASSES[variant], className)}>
      {icon && VARIANT_ICON[variant]}
      <span>{children}</span>
    </div>
  );
}
