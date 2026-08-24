// Overlay shared by every modal/form sheet in the app. Renders as a true full-screen page on small
// viewports (no floating card that content can push past the screen edge) and as a centered card on
// larger ones. Header and footer are separate non-scrolling zones so the close button and the primary
// action are always reachable, however long the middle content gets — this is the fix for forms
// overflowing the viewport (or burying the Save button) on narrow phones.
import type { ReactNode } from "react";
import { cn } from "../utils/cn";
import { CloseIcon } from "../icons";
import { TONES, type Tone } from "../theme/tones";

interface ModalProps {
  onClose: () => void;
  children: ReactNode;
  footer?: ReactNode;
  className?: string;
}

export function Modal({ onClose, children, footer, className }: ModalProps) {
  return (
    <div
      className="fixed inset-0 z-[999] flex items-center justify-center bg-black/40 backdrop-blur-[8px] p-0 sm:p-4"
      style={{ height: "100dvh" }}
      onClick={onClose}
    >
      <div
        className={cn(
          "bg-white w-full max-w-[420px] h-[100dvh] sm:h-auto sm:max-h-[85vh] flex flex-col box-border overflow-hidden",
          "rounded-none border-0 sm:rounded-[24px] sm:border sm:border-[#f2f2f7] sm:shadow-[0_20px_50px_rgba(0,0,0,0.25)]",
          className,
        )}
        onClick={(e) => e.stopPropagation()}
      >
        <div className="flex-1 min-h-0 overflow-y-auto overflow-x-hidden overscroll-contain px-5 pt-5">
          {children}
          {!footer && <div className="pb-[max(20px,env(safe-area-inset-bottom))]" />}
        </div>
        {footer && (
          <div className="shrink-0 px-5 pt-3.5 pb-[max(16px,env(safe-area-inset-bottom))] border-t border-[#f2f2f7] bg-white">
            {footer}
          </div>
        )}
      </div>
    </div>
  );
}

interface ModalHeaderProps {
  title: string;
  subtitle?: string;
  icon?: ReactNode;
  tone?: Tone;
  badge?: ReactNode;
  onClose?: () => void;
}

export function ModalHeader({ title, subtitle, icon, tone = "blue", badge, onClose }: ModalHeaderProps) {
  const t = TONES[tone];
  return (
    <div className="sticky top-0 z-10 -mx-5 px-5 bg-white pb-4 flex items-start justify-between gap-3">
      <div className="flex items-center gap-3 min-w-0">
        {icon && <div className={cn("shrink-0 w-11 h-11 rounded-2xl flex items-center justify-center", t.soft, t.text)}>{icon}</div>}
        <div className="min-w-0">
          <h3 className="m-0 text-lg font-bold tracking-[-0.4px] text-[#1c1c1e]">{title}</h3>
          {subtitle && <p className="m-0 mt-0.5 text-xs text-[#8e8e93] font-medium">{subtitle}</p>}
        </div>
      </div>
      <div className="flex items-center gap-2 shrink-0">
        {badge}
        {onClose && (
          <button
            type="button"
            onClick={onClose}
            aria-label="Kapat"
            className="h-8 w-8 rounded-full flex items-center justify-center text-[#8e8e93] bg-[#f2f2f7] hover:bg-[#e5e5ea] hover:text-[#1c1c1e] transition-colors border-none cursor-pointer"
          >
            <CloseIcon className="h-4 w-4" />
          </button>
        )}
      </div>
    </div>
  );
}
