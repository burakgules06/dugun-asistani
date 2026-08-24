// Centralized icon registry. Every icon used to be redefined locally per file (Dashboard alone had
// six, ServicesView another seven); this is now the single place they're drawn from. Each icon exposes
// a `className` override (replaces, not merges, the default) so callers can restyle size/color per use.
import type { SVGProps } from "react";

export interface IconProps {
  className?: string;
}

function svg(defaultClassName: string, props: SVGProps<SVGSVGElement>, className?: string) {
  return { ...props, className: className ?? defaultClassName };
}

export function MenuDotsIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#4b5563]", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 1.75, strokeLinecap: "round" }, className)} aria-hidden="true">
      <line x1="3" y1="5" x2="17" y2="5" />
      <line x1="3" y1="10" x2="17" y2="10" />
      <line x1="3" y1="15" x2="17" y2="15" />
    </svg>
  );
}

export function HomeIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#2563eb]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)} aria-hidden="true">
      <path d="M9.56 2.53a1 1 0 0 1 .88 0l6.5 3.58A1 1 0 0 1 17.5 7v8a2 2 0 0 1-2 2H4.5a2 2 0 0 1-2-2V7a1 1 0 0 1 .56-.89l6.5-3.58ZM8 12a1 1 0 0 0-1 1v4h6v-4a1 1 0 0 0-1-1H8Z" />
    </svg>
  );
}

export function ServicesIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#6366f1]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)} aria-hidden="true">
      <path d="M3 4.5A1.5 1.5 0 0 1 4.5 3h11A1.5 1.5 0 0 1 17 4.5v2A1.5 1.5 0 0 1 15.5 8h-11A1.5 1.5 0 0 1 3 6.5v-2Zm0 9A1.5 1.5 0 0 1 4.5 12h11a1.5 1.5 0 0 1 1.5 1.5v2a1.5 1.5 0 0 1-1.5 1.5h-11A1.5 1.5 0 0 1 3 15.5v-2Z" />
    </svg>
  );
}

export function CalendarIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#007ff5]", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 1.5, strokeLinecap: "round", strokeLinejoin: "round" }, className)} aria-hidden="true">
      <rect x="3" y="4.5" width="14" height="12.5" rx="2" />
      <line x1="3" y1="8" x2="17" y2="8" />
      <line x1="6.5" y1="2.5" x2="6.5" y2="5.5" />
      <line x1="13.5" y1="2.5" x2="13.5" y2="5.5" />
    </svg>
  );
}

export function ClockIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#0ea5e9]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)} aria-hidden="true">
      <path fillRule="evenodd" d="M10 18a8 8 0 1 0 0-16 8 8 0 0 0 0 16Zm.75-11a.75.75 0 0 0-1.5 0v3.5c0 .2.08.39.22.53l2 2a.75.75 0 1 0 1.06-1.06l-1.78-1.78V7Z" clipRule="evenodd" />
    </svg>
  );
}

export function RepeatIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#f59e0b]", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 1.75, strokeLinecap: "round", strokeLinejoin: "round" }, className)} aria-hidden="true">
      <path d="M3 8a5 5 0 0 1 5-5h6M17 12a5 5 0 0 1-5 5H6" />
      <path d="M11 1l3 2-3 2M9 19l-3-2 3-2" />
    </svg>
  );
}

export function LogoutIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#dc2626]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)} aria-hidden="true">
      <path fillRule="evenodd" d="M3 4.75A1.75 1.75 0 0 1 4.75 3h5.5a.75.75 0 0 1 0 1.5h-5.5a.25.25 0 0 0-.25.25v10.5c0 .14.11.25.25.25h5.5a.75.75 0 0 1 0 1.5h-5.5A1.75 1.75 0 0 1 3 15.25V4.75Zm9.03 2.22a.75.75 0 0 1 1.06 0l2.5 2.5a.75.75 0 0 1 0 1.06l-2.5 2.5a.75.75 0 1 1-1.06-1.06l1.22-1.22H8.75a.75.75 0 0 1 0-1.5h4.5l-1.22-1.22a.75.75 0 0 1 0-1.06Z" clipRule="evenodd" />
    </svg>
  );
}

export function UserGroupIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-emerald-500", { viewBox: "0 0 20 20", fill: "currentColor" }, className)} aria-hidden="true">
      <path d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z" />
    </svg>
  );
}

export function LockIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-orange-500", { viewBox: "0 0 20 20", fill: "currentColor" }, className)} aria-hidden="true">
      <path fillRule="evenodd" d="M10 1a4.5 4.5 0 00-4.5 4.42V7H5a2 2 0 00-2 2v8a2 2 0 002 2h10a2 2 0 002-2V9a2 2 0 00-2-2h-.5V5.42A4.5 4.5 0 0010 1zm2.5 6V5.42a2.5 2.5 0 00-5 0V7h5zM10 11a1 1 0 100 2 1 1 0 000-2z" clipRule="evenodd" />
    </svg>
  );
}

export function CloseIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 2, strokeLinecap: "round" }, className)}>
      <line x1="5" y1="5" x2="15" y2="15" />
      <line x1="15" y1="5" x2="5" y2="15" />
    </svg>
  );
}

export function PlusIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 2, strokeLinecap: "round" }, className)}>
      <line x1="10" y1="4" x2="10" y2="16" />
      <line x1="4" y1="10" x2="16" y2="10" />
    </svg>
  );
}

export function PhoneIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#16a34a]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)} aria-hidden="true">
      <path d="M4.5 2.5A1.5 1.5 0 0 0 3 4c0 7.18 5.82 13 13 13a1.5 1.5 0 0 0 1.5-1.5v-2.11a1.5 1.5 0 0 0-1.23-1.48l-2.68-.48a1.5 1.5 0 0 0-1.42.47l-.9 1a10.05 10.05 0 0 1-4.66-4.66l1-.9c.4-.36.56-.9.47-1.42l-.48-2.68A1.5 1.5 0 0 0 6.11 2.5H4.5Z" />
    </svg>
  );
}

export function NoteIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#b45309]", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 1.5, strokeLinecap: "round", strokeLinejoin: "round" }, className)} aria-hidden="true">
      <path d="M4 3.5h9.5L16 6v10.5a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V4.5a1 1 0 0 1 1-1Z" />
      <path d="M13 3.5V6a1 1 0 0 0 1 1h2" fill="none" />
      <path d="M6 10.5h6M6 13.5h4" />
    </svg>
  );
}

export function UsersIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-3 w-3", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path d="M7 8a3 3 0 1 0 0-6 3 3 0 0 0 0 6Zm6.5-.5a2.5 2.5 0 1 0 0-5 2.5 2.5 0 0 0 0 5ZM7 9c-2.67 0-6 1.34-6 4v2h9.5v-2c0-.94.34-1.84.94-2.6C10.24 9.5 8.6 9 7 9Zm6.5 1c-.3 0-.6.02-.88.06.86.9 1.38 2.03 1.38 3.44V16H19v-1.5c0-2.16-2.7-3.5-5.5-3.5Z" />
    </svg>
  );
}

export function EyeIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-5 w-5 text-[#b45309]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path d="M10 3.5c-4.5 0-7.5 4.5-7.5 6.5s3 6.5 7.5 6.5 7.5-4.5 7.5-6.5-3-6.5-7.5-6.5Zm0 10.5a4 4 0 1 1 0-8 4 4 0 0 1 0 8Z" />
      <circle cx="10" cy="10" r="1.75" />
    </svg>
  );
}

export function InboxIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-6 w-6 text-[#8e8e93]", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 1.5, strokeLinecap: "round", strokeLinejoin: "round" }, className)}>
      <path d="M3 12l2.5-7h9L17 12M3 12v4a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-4M3 12h4.5a2.5 2.5 0 0 0 5 0H17" />
    </svg>
  );
}

export function GripIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4", { viewBox: "0 0 20 20", fill: "currentColor", style: { touchAction: "none" } }, className)}>
      <circle cx="7" cy="5" r="1.4" />
      <circle cx="13" cy="5" r="1.4" />
      <circle cx="7" cy="10" r="1.4" />
      <circle cx="13" cy="10" r="1.4" />
      <circle cx="7" cy="15" r="1.4" />
      <circle cx="13" cy="15" r="1.4" />
    </svg>
  );
}

export function PersonalIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-3.5 w-3.5 text-[#9333ea]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path d="M10 9a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7Zm-6 8a6 6 0 1 1 12 0 1 1 0 0 1-1 1H5a1 1 0 0 1-1-1Z" />
    </svg>
  );
}

export function LockClosedIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-3 w-3 text-[#c7c7cc]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path fillRule="evenodd" d="M10 1a4 4 0 0 0-4 4v2H5a2 2 0 0 0-2 2v7a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2v-7a2 2 0 0 0-2-2h-1V5a4 4 0 0 0-4-4Zm2 6V5a2 2 0 1 0-4 0v2h4Z" clipRule="evenodd" />
    </svg>
  );
}

export function ChevronLeftIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path fillRule="evenodd" d="M12.79 15.53a.75.75 0 0 1-1.06 0l-5.5-5.5a.75.75 0 0 1 0-1.06l5.5-5.5a.75.75 0 1 1 1.06 1.06L7.81 9.5l4.98 4.97a.75.75 0 0 1 0 1.06Z" clipRule="evenodd" />
    </svg>
  );
}

export function ChevronRightIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path fillRule="evenodd" d="M7.21 4.47a.75.75 0 0 1 1.06 0l5.5 5.5a.75.75 0 0 1 0 1.06l-5.5 5.5a.75.75 0 1 1-1.06-1.06L12.19 10.5 7.21 5.53a.75.75 0 0 1 0-1.06Z" clipRule="evenodd" />
    </svg>
  );
}

export function ChevronDownIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-3 w-3", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path fillRule="evenodd" d="M5.23 7.21a.75.75 0 0 1 1.06.02L10 11.168l3.71-3.938a.75.75 0 1 1 1.08 1.04l-4.25 4.5a.75.75 0 0 1-1.08 0l-4.25-4.5a.75.75 0 0 1 .02-1.06Z" clipRule="evenodd" />
    </svg>
  );
}

export function WarningIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#c2410c]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path fillRule="evenodd" d="M8.485 2.495c.673-1.166 2.357-1.166 3.03 0l6.28 10.875c.673 1.167-.17 2.63-1.516 2.63H3.72c-1.347 0-2.189-1.463-1.516-2.63L8.485 2.495ZM10 6a.75.75 0 0 1 .75.75v3.5a.75.75 0 0 1-1.5 0v-3.5A.75.75 0 0 1 10 6Zm0 8a1 1 0 1 0 0-2 1 1 0 0 0 0 2Z" clipRule="evenodd" />
    </svg>
  );
}

export function CheckCircleIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#10b981]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path fillRule="evenodd" d="M10 18a8 8 0 1 0 0-16 8 8 0 0 0 0 16Zm3.857-9.809a.75.75 0 0 0-1.214-.882l-3.483 4.79-1.88-1.88a.75.75 0 1 0-1.06 1.061l2.5 2.5a.75.75 0 0 0 1.137-.089l4-5.5Z" clipRule="evenodd" />
    </svg>
  );
}

export function InfoIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#1d4ed8]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path fillRule="evenodd" d="M18 10A8 8 0 1 1 2 10a8 8 0 0 1 16 0ZM9 9a1 1 0 0 1 1-1h.01a1 1 0 1 1 0 2H10a1 1 0 0 1-1-1Zm0 3a1 1 0 1 0 0 2h2a1 1 0 1 0 0-2H9Z" clipRule="evenodd" />
    </svg>
  );
}

export function BellIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#d97706]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)} aria-hidden="true">
      <path fillRule="evenodd" d="M10 2a1 1 0 0 1 1 1v.34a5.5 5.5 0 0 1 4.5 5.41v2.05c0 .74.29 1.45.82 1.98l.32.32a1 1 0 0 1-.71 1.7H4.07a1 1 0 0 1-.71-1.7l.32-.32c.53-.53.82-1.24.82-1.98V8.75A5.5 5.5 0 0 1 9 3.34V3a1 1 0 0 1 1-1Zm-2 14.5a2 2 0 0 0 4 0h-4Z" clipRule="evenodd" />
    </svg>
  );
}

export function BellOffIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#9ca3af]", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 1.5, strokeLinecap: "round", strokeLinejoin: "round" }, className)} aria-hidden="true">
      <path d="M6.5 6.5V8.75c0 .74-.29 1.45-.82 1.98l-.32.32a1 1 0 0 0 .71 1.7h9.36M9 3.42A5.5 5.5 0 0 1 14.5 8.75v2.05c0 .29.05.57.14.83" fill="none" />
      <path d="M8 16.5a2 2 0 0 0 4 0" fill="none" />
      <line x1="3" y1="3" x2="17" y2="17" />
    </svg>
  );
}

export function SearchIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#007ff5]", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 1.75, strokeLinecap: "round", strokeLinejoin: "round" }, className)} aria-hidden="true">
      <circle cx="9" cy="9" r="6" />
      <line x1="17" y1="17" x2="13.4" y2="13.4" />
    </svg>
  );
}

export function KebabIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-4 w-4 text-[#8e8e93]", { viewBox: "0 0 20 20", fill: "currentColor" }, className)} aria-hidden="true">
      <circle cx="10" cy="4" r="1.6" />
      <circle cx="10" cy="10" r="1.6" />
      <circle cx="10" cy="16" r="1.6" />
    </svg>
  );
}

export function PencilIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-3.5 w-3.5 text-[#6b7280]", { viewBox: "0 0 20 20", fill: "none", stroke: "currentColor", strokeWidth: 1.6, strokeLinecap: "round", strokeLinejoin: "round" }, className)} aria-hidden="true">
      <path d="M13.4 3.3a1.5 1.5 0 0 1 2.12 2.12L7.5 13.44l-3.2.9.9-3.2L13.4 3.3Z" />
    </svg>
  );
}

export function TrashIcon({ className }: IconProps) {
  return (
    <svg {...svg("h-3.5 w-3.5", { viewBox: "0 0 20 20", fill: "currentColor" }, className)}>
      <path fillRule="evenodd" d="M8.75 1A2.75 2.75 0 0 0 6 3.75v.443c-.795.077-1.584.176-2.365.298a.75.75 0 1 0 .23 1.482l.149-.022.841 10.518A2.75 2.75 0 0 0 7.596 19h4.807a2.75 2.75 0 0 0 2.742-2.53l.841-10.52.149.023a.75.75 0 0 0 .23-1.482 41.03 41.03 0 0 0-2.365-.298V3.75A2.75 2.75 0 0 0 11.25 1h-2.5ZM10 4c.84 0 1.673.025 2.5.075V3.75c0-.69-.56-1.25-1.25-1.25h-2.5c-.69 0-1.25.56-1.25 1.25v.325C8.327 4.025 9.16 4 10 4Z" clipRule="evenodd" />
    </svg>
  );
}
