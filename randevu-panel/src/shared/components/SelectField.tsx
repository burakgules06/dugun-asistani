// Same field chrome as TextField, applied to <select>. The dropdown arrow is a real icon instead of
// an embedded base64 SVG background-image, so it recolors/resizes like any other icon in the kit.
import { forwardRef, type ReactNode, type SelectHTMLAttributes } from "react";
import { cn } from "../utils/cn";
import { ChevronDownIcon } from "../icons";
import { TONES, type Tone } from "../theme/tones";

interface SelectFieldProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string;
  error?: string;
  leading?: ReactNode;
  tone?: Tone;
  children: ReactNode;
}

const FIELD_BASE =
  "w-full px-3 py-2.5 pr-9 rounded-xl border border-[#e5e5ea] bg-[#f9f9f9] box-border outline-none text-base md:text-sm text-black transition-all focus:bg-white focus:ring-4 appearance-none disabled:opacity-60 disabled:cursor-not-allowed";

export const SelectField = forwardRef<HTMLSelectElement, SelectFieldProps>(function SelectField(
  { label, error, leading, tone = "blue", className, children, ...rest },
  ref,
) {
  const t = TONES[tone];
  return (
    <label className="flex flex-col gap-1.5">
      {label && <span className="text-[11px] font-semibold text-[#8e8e93] uppercase tracking-wide">{label}</span>}
      <span className="relative flex items-center">
        <select
          ref={ref}
          className={cn(
            FIELD_BASE,
            error ? "focus:border-[#ff3b30] focus:ring-[#ff3b30]/10" : cn(t.focusBorder, t.ring),
            Boolean(leading) && "pl-10",
            error && "border-[#ff3b30]",
            className,
          )}
          {...rest}
        >
          {children}
        </select>
        {leading && <span className="absolute left-3 flex items-center">{leading}</span>}
        <ChevronDownIcon className="pointer-events-none absolute right-3 h-3 w-3 text-[#8e8e93]" />
      </span>
      {error && <span className="text-xs font-medium text-[#ff3b30]">{error}</span>}
    </label>
  );
});
