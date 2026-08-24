// Labeled input with one source of truth for border/radius/focus-ring styling, replacing the ~15
// near-identical Tailwind class strings (and inline style objects) previously copy-pasted per form.
import { forwardRef, type InputHTMLAttributes, type ReactNode } from "react";
import { cn } from "../utils/cn";
import { TONES, type Tone } from "../theme/tones";

interface TextFieldProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  leading?: ReactNode;
  trailing?: ReactNode;
  tone?: Tone;
}

const FIELD_BASE =
  "w-full px-3 py-2.5 rounded-xl border border-[#e5e5ea] bg-[#f9f9f9] box-border outline-none text-base md:text-sm text-black transition-all focus:bg-white focus:ring-4 placeholder:text-[#8e8e93] disabled:opacity-60 disabled:cursor-not-allowed";

export const TextField = forwardRef<HTMLInputElement, TextFieldProps>(function TextField(
  { label, error, leading, trailing, tone = "blue", className, ...rest },
  ref,
) {
  const t = TONES[tone];
  return (
    <label className="flex flex-col gap-1.5">
      {label && <span className="text-[11px] font-semibold text-[#8e8e93] uppercase tracking-wide">{label}</span>}
      <span className="relative flex items-center">
        <input
          ref={ref}
          className={cn(
            FIELD_BASE,
            error ? "focus:border-[#ff3b30] focus:ring-[#ff3b30]/10" : cn(t.focusBorder, t.ring),
            Boolean(leading) && "pl-10",
            Boolean(trailing) && "pr-14",
            Boolean(error) && "border-[#ff3b30]",
            className,
          )}
          {...rest}
        />
        {leading && <span className="absolute left-3 flex items-center">{leading}</span>}
        {trailing && <span className="absolute right-3">{trailing}</span>}
      </span>
      {error && <span className="text-xs font-medium text-[#ff3b30]">{error}</span>}
    </label>
  );
});
