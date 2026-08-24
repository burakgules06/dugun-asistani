// Multi-line counterpart to TextField, sharing its border/radius/focus-ring language.
import { forwardRef, type ReactNode, type TextareaHTMLAttributes } from "react";
import { cn } from "../utils/cn";
import { TONES, type Tone } from "../theme/tones";

interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string;
  error?: string;
  leading?: ReactNode;
  tone?: Tone;
}

const FIELD_BASE =
  "w-full px-3 py-2.5 rounded-xl border border-[#e5e5ea] bg-[#f9f9f9] box-border outline-none text-base md:text-sm text-black transition-all focus:bg-white focus:ring-4 placeholder:text-[#8e8e93] disabled:opacity-60 disabled:cursor-not-allowed resize-none";

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(function Textarea(
  { label, error, leading, tone = "blue", className, ...rest },
  ref,
) {
  const t = TONES[tone];
  return (
    <label className="flex flex-col gap-1.5">
      {label && <span className="text-[11px] font-semibold text-[#8e8e93] uppercase tracking-wide">{label}</span>}
      <span className="relative flex items-start">
        <textarea
          ref={ref}
          className={cn(
            FIELD_BASE,
            error ? "border-[#ff3b30] focus:border-[#ff3b30] focus:ring-[#ff3b30]/10" : cn(t.focusBorder, t.ring),
            Boolean(leading) && "pl-10",
            className,
          )}
          {...rest}
        />
        {leading && <span className="absolute left-3 top-2.5">{leading}</span>}
      </span>
      {error && <span className="text-xs font-medium text-[#ff3b30]">{error}</span>}
    </label>
  );
});
