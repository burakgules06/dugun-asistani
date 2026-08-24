import type { Menu } from "../../shared/types";

// Menülere kararlı, tekrarlanabilir renk ataması: aynı menü her zaman aynı rengi alır
// (menü listesindeki sırasına göre bir palet döngüsü).
const PALETTE = ["#007ff5", "#9333ea", "#22c55e", "#ff3b30", "#f59e0b", "#0ea5e9", "#ec4899", "#84cc16"];

export function buildMenuColorMap(menus: Menu[]): Map<string, string> {
  const map = new Map<string, string>();
  menus.forEach((m, idx) => map.set(m.id, PALETTE[idx % PALETTE.length]));
  return map;
}

export const NO_MENU_COLOR = "#c7c7cc";
