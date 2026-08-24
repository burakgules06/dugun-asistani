export const getDayName = (dayOfWeek: number): string => {
  const names = ["", "Pazartesi", "Salı", "Çarşamba", "Perşembe", "Cuma", "Cumartesi", "Pazar"];
  return names[dayOfWeek] || "";
};

// Bugünün tarihini ISO formatında (YYYY-MM-DD) döner
export function todayIso(): string {
  return new Date().toLocaleDateString("sv-SE");
}

// Gün kaydırma işlevini yapar
export function shiftDate(dateStr: string, days: number): string {
  const d = new Date(dateStr + "T00:00:00");
  d.setDate(d.getDate() + days);
  return d.toLocaleDateString("sv-SE");
}

export function getDateLabel(
  iso: string,
  apiLocale = "tr-TR",
  labels?: { today?: string; tomorrow?: string; yesterday?: string },
): string {
  const today = todayIso();
  const d = new Date(iso + "T00:00:00");
  const t = new Date(today + "T00:00:00");
  const diffDays = Math.round((d.getTime() - t.getTime()) / 86400000);

  const weekday = d.toLocaleDateString(apiLocale, { weekday: "long" });
  const dayMonth = d.toLocaleDateString(apiLocale, { day: "numeric", month: "long" });

  let prefix = "";
  if (diffDays === 0) prefix = `${labels?.today ?? "Bugün"} · `;
  else if (diffDays === 1) prefix = `${labels?.tomorrow ?? "Yarın"} · `;
  else if (diffDays === -1) prefix = `${labels?.yesterday ?? "Dün"} · `;

  return `${prefix}${dayMonth} ${weekday}`;
}

// Süreyi formatlar (Örn: 90 -> 1 Saat 30 dk)
export function formatDuration(min: number): string {
  if (min < 60) return `${min} dk`;
  const h = Math.floor(min / 60);
  const m = min % 60;
  return m === 0 ? `${h} Saat` : `${h} Saat ${m} dk`;
}

// src/utils/date.ts (veya projedeki konumuna göre)
export function toLocalDate(s: string): Date {
  const [y, m, d] = s.split("-").map(Number);
  return new Date(y, m - 1, d, 12);
}