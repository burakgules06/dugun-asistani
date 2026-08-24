// Text back-navigation control reused wherever a screen returns to the previous view.
import { ChevronLeftIcon } from "../icons";

export function BackButton({ label, onClick }: { label: string; onClick: () => void }) {
  return (
    <button
      onClick={onClick}
      className="flex items-center gap-1 border-none bg-transparent text-[#007ff5] text-[15px] font-medium cursor-pointer pb-4 pt-0 px-0"
      style={{ WebkitTapHighlightColor: "transparent" }}
    >
      <ChevronLeftIcon className="h-4 w-4" />
      {label}
    </button>
  );
}
