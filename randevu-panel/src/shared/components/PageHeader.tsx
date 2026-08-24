// Eyebrow label + title pair used atop weekly/monthly/services screens.
interface PageHeaderProps {
  eyebrow: string;
  title: string;
}

export function PageHeader({ eyebrow, title }: PageHeaderProps) {
  return (
    <div className="px-1 pb-1">
      <p className="text-[11px] font-bold tracking-[1.5px] uppercase text-[#007ff5] m-0">{eyebrow}</p>
      <h2 className="text-2xl font-bold tracking-[-0.5px] text-[#1c1c1e] mt-1 m-0">{title}</h2>
    </div>
  );
}
