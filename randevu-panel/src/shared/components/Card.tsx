// Reusable white surface: the base visual container behind every list item, form panel, and card.
import { forwardRef, type HTMLAttributes } from "react";
import { cn } from "../utils/cn";

export const Card = forwardRef<HTMLDivElement, HTMLAttributes<HTMLDivElement>>(function Card(
  { className, ...rest },
  ref,
) {
  return <div ref={ref} className={cn("rounded-2xl border border-[#f0f0f0] bg-white shadow-sm", className)} {...rest} />;
});
