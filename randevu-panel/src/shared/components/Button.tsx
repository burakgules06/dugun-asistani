// Variant/tone-driven primitive: `variant` picks the shape (solid gradient, neutral, text-only, soft
// pill), `tone` picks the color. Every button in the app should render through this instead of a
// one-off className, so a differently-colored action becomes a prop change, not new markup.
import { forwardRef, type ButtonHTMLAttributes } from "react";
import { cn } from "../utils/cn";
import { TONES, type Tone } from "../theme/tones";

export type ButtonVariant = "primary" | "secondary" | "ghost" | "soft";
export type ButtonSize = "sm" | "md";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
  tone?: Tone;
  size?: ButtonSize;
  fullWidth?: boolean;
  isLoading?: boolean;
}

const SIZE_CLASSES: Record<ButtonSize, string> = {
  sm: "py-2 px-3 text-xs rounded-lg",
  md: "py-2.5 px-4 text-sm rounded-xl",
};

export const Button = forwardRef<HTMLButtonElement, ButtonProps>(function Button(
  { variant = "primary", tone = "blue", size = "md", fullWidth = false, isLoading = false, disabled, className, children, ...rest },
  ref,
) {
  const t = TONES[tone];
  const isGhost = variant === "ghost";

  const variantClasses =
    variant === "primary"
      ? cn("border-none text-white bg-gradient-to-r", t.gradientFrom, t.gradientTo, t.shadow, "hover:opacity-95 active:scale-[0.99]")
      : variant === "secondary"
        ? "border-none bg-[#f2f2f7] text-[#48484a] hover:bg-[#e5e5ea]"
        : variant === "soft"
          ? cn("border", t.soft, t.softText, t.softBorder, "hover:brightness-95")
          : cn("border-none bg-transparent px-0 py-1", t.text, "hover:opacity-75");

  return (
    <button
      ref={ref}
      disabled={disabled || isLoading}
      className={cn(
        "inline-flex items-center justify-center gap-1.5 font-semibold cursor-pointer transition-all select-none disabled:opacity-50 disabled:cursor-not-allowed",
        !isGhost && SIZE_CLASSES[size],
        isGhost && (size === "sm" ? "text-xs" : "text-[15px]"),
        variantClasses,
        fullWidth && "w-full",
        className,
      )}
      style={{ WebkitTapHighlightColor: "transparent" }}
      {...rest}
    >
      {children}
    </button>
  );
});
