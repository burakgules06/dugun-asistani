import { useCallback, useEffect, useState } from "react";
import { createMenu, getMenus, setMenuActive, updateMenu } from "../../api/menus";
import { useResolveError } from "../../shared/errors";
import type { Menu } from "../../shared/types";

export type MenuUpsertData = { name: string; description: string | null; pricePerPerson: number | null; hallIds: string[] };

export function useMenus() {
  const resolve = useResolveError();
  const [menus, setMenus] = useState<Menu[] | null>(null);
  const [error, setError] = useState("");

  const load = useCallback(() => {
    getMenus(true)
      .then(setMenus)
      .catch((e) => setError(resolve(e)));
  }, [resolve]);

  useEffect(load, [load]);

  async function create(data: MenuUpsertData) {
    const created = await createMenu(data);
    setMenus((prev) => (prev ? [...prev, created] : [created]));
  }

  async function update(id: string, data: MenuUpsertData) {
    const updated = await updateMenu(id, data);
    setMenus((prev) => prev?.map((m) => (m.id === id ? updated : m)) ?? null);
  }

  async function toggleActive(menu: Menu) {
    setMenus((prev) => prev?.map((m) => (m.id === menu.id ? { ...m, active: !m.active } : m)) ?? null);
    try {
      await setMenuActive(menu.id, !menu.active);
    } catch {
      load();
    }
  }

  return { menus, error, load, create, update, toggleActive };
}
