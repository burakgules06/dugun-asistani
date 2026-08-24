import { useCallback, useEffect, useState } from "react";
import { createHall, getHalls, setHallActive, updateHall, type HallUpsert } from "../../api/halls";
import { useResolveError } from "../../shared/errors";
import type { Hall } from "../../shared/types";

export function useHalls() {
  const resolve = useResolveError();
  const [halls, setHalls] = useState<Hall[] | null>(null);
  const [error, setError] = useState("");

  const load = useCallback(() => {
    getHalls(true)
      .then(setHalls)
      .catch((e) => setError(resolve(e)));
  }, [resolve]);

  useEffect(load, [load]);

  async function create(data: HallUpsert) {
    const created = await createHall(data);
    setHalls((prev) => (prev ? [...prev, created] : [created]));
  }

  async function update(id: string, data: HallUpsert) {
    const updated = await updateHall(id, data);
    setHalls((prev) => prev?.map((h) => (h.id === id ? updated : h)) ?? null);
  }

  async function toggleActive(hall: Hall) {
    setHalls((prev) => prev?.map((h) => (h.id === hall.id ? { ...h, active: !h.active } : h)) ?? null);
    try {
      await setHallActive(hall.id, !hall.active);
    } catch {
      load();
    }
  }

  return { halls, error, load, create, update, toggleActive };
}
