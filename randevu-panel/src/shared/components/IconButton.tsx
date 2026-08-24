// Circular icon-only action button (calendar nav, dismiss controls) — tone-driven like Button.
import { forwardRef, type ButtonHTMLAttributes, type ReactNode } from "react";
import { cn } from "../utils/cn";
import { TONES, type Tone } from "../theme/tones";

interface IconButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  tone?: Tone;
  size?: number;
  icon: ReactNode;
  "aria-label": string;
}

export const IconButton = forwardRef<HTMLButtonElement, IconButtonProps>(function IconButton(
  { tone = "blue", size = 40, icon, className, style, ...rest },
  ref,
) {
  const t = TONES[tone];
  return (
    <button
      ref={ref}
      className={cn(
        "inline-flex items-center justify-center shrink-0 rounded-full border-none cursor-pointer transition-opacity active:opacity-75 bg-[#f2f2f7]",
        t.text,
        "disabled:opacity-50 disabled:cursor-not-allowed",
        className,
      )}
      style={{ width: size, height: size, WebkitTapHighlightColor: "transparent", ...style }}
      {...rest}
    >
      {icon}
    </button>
  );
});
