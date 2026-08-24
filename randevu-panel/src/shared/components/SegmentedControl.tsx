// Pill-style segmented control — extracted after noticing the tab bar and the admin hours-mode
// switch were two near-identical hand-rolled implementations of the same control.
import { cn } from "../utils/cn";
import { TONES, type Tone } from "../theme/tones";

interface SegmentedControlOption<T extends string> {
  value: T;
  label: string;
}

interface SegmentedControlProps<T extends string> {
  options: SegmentedControlOption<T>[];
  value: T;
  onChange: (value: T) => void;
  tone?: Tone;
  size?: "sm" | "md";
}

export function SegmentedControl<T extends string>({ options, value, onChange, tone = "blue", size = "md" }: SegmentedControlProps<T>) {
  const t = TONES[tone];

  return (
    <div className={cn("flex bg-[#f4f4f5] rounded-2xl", size === "sm" ? "p-1 gap-1" : "p-[3px]")}>
      {options.map((option) => {
        const active = option.value === value;
        return (
          <button
            key={option.value}
            type="button"
            onClick={() => onChange(option.value)}
            className={cn(
              "flex-1 border-none rounded-[11px] cursor-pointer transition-all outline-none font-semibold",
              size === "sm" ? "py-2 text-xs" : "py-2.5 text-xs",
              active ? cn("bg-white shadow-[0_2px_6px_rgba(0,0,0,0.06)]", t.text) : "bg-transparent text-[#71717a]",
            )}
            style={{ WebkitTapHighlightColor: "transparent" }}
          >
            {option.label}
          </button>
        );
      })}
    </div>
  );
}
