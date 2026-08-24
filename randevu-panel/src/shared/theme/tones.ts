// Tone token map: one semantic color per name, consumed by Button/Badge/Banner/IconButton.
// Anywhere a component previously hardcoded a gradient/hex per call site, it now takes `tone` instead.
export type Tone = "blue" | "purple" | "green" | "red" | "amber" | "gray";

export interface ToneDefinition {
  gradientFrom: string;
  gradientTo: string;
  shadow: string;
  soft: string;
  softText: string;
  softBorder: string;
  solidBg: string;
  solidHex: string;
  text: string;
  ring: string;
  focusBorder: string;
}

export const TONES: Record<Tone, ToneDefinition> = {
  blue: {
    gradientFrom: "from-[#007ff5]",
    gradientTo: "to-[#006ee0]",
    shadow: "shadow-[0_4px_12px_rgba(0,127,245,0.2)]",
    soft: "bg-[#eff6ff]",
    softText: "text-[#007ff5]",
    softBorder: "border-[#dbeafe]",
    solidBg: "bg-[#007ff5]",
    solidHex: "#007ff5",
    text: "text-[#007ff5]",
    ring: "focus:ring-[#007ff5]/10",
    focusBorder: "focus:border-[#007ff5]",
  },
  purple: {
    gradientFrom: "from-[#a855f7]",
    gradientTo: "to-[#9333ea]",
    shadow: "shadow-[0_4px_12px_rgba(147,51,234,0.25)]",
    soft: "bg-[#f3e8ff]",
    softText: "text-[#9333ea]",
    softBorder: "border-[#e9d5ff]",
    solidBg: "bg-[#9333ea]",
    solidHex: "#9333ea",
    text: "text-[#9333ea]",
    ring: "focus:ring-[#9333ea]/10",
    focusBorder: "focus:border-[#9333ea]",
  },
  green: {
    gradientFrom: "from-[#22c55e]",
    gradientTo: "to-[#16a34a]",
    shadow: "shadow-[0_4px_12px_rgba(34,197,94,0.2)]",
    soft: "bg-[#f0fdf4]",
    softText: "text-[#16a34a]",
    softBorder: "border-[#dcfce7]",
    solidBg: "bg-[#22c55e]",
    solidHex: "#22c55e",
    text: "text-[#16a34a]",
    ring: "focus:ring-[#22c55e]/10",
    focusBorder: "focus:border-[#22c55e]",
  },
  red: {
    gradientFrom: "from-[#ff3b30]",
    gradientTo: "to-[#e0281e]",
    shadow: "shadow-[0_4px_12px_rgba(255,59,48,0.2)]",
    soft: "bg-[#fff4f4]",
    softText: "text-[#c2410c]",
    softBorder: "border-[#ffd0d0]",
    solidBg: "bg-[#ff3b30]",
    solidHex: "#ff3b30",
    text: "text-[#ff3b30]",
    ring: "focus:ring-[#ff3b30]/10",
    focusBorder: "focus:border-[#ff3b30]",
  },
  amber: {
    gradientFrom: "from-[#f59e0b]",
    gradientTo: "to-[#d97706]",
    shadow: "shadow-[0_4px_12px_rgba(245,158,11,0.2)]",
    soft: "bg-[#fef3c7]",
    softText: "text-[#b45309]",
    softBorder: "border-[#fde68a]",
    solidBg: "bg-[#f59e0b]",
    solidHex: "#f59e0b",
    text: "text-[#b45309]",
    ring: "focus:ring-[#f59e0b]/10",
    focusBorder: "focus:border-[#f59e0b]",
  },
  gray: {
    gradientFrom: "from-[#8e8e93]",
    gradientTo: "to-[#6b6b70]",
    shadow: "shadow-[0_4px_12px_rgba(142,142,147,0.15)]",
    soft: "bg-[#f2f2f7]",
    softText: "text-[#48484a]",
    softBorder: "border-[#e5e5ea]",
    solidBg: "bg-[#8e8e93]",
    solidHex: "#8e8e93",
    text: "text-[#48484a]",
    ring: "focus:ring-[#8e8e93]/10",
    focusBorder: "focus:border-[#8e8e93]",
  },
};
