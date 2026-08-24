import { useCallback, useEffect, useState } from "react";
import { createCapacityRule, deleteCapacityRule, getCapacityRules, updateCapacityRule, type CapacityRuleUpsert } from "../../api/capacityRules";
import { useResolveError } from "../../shared/errors";
import type { CapacityRule } from "../../shared/types";

export function useCapacityRules() {
  const resolve = useResolveError();
  const [rules, setRules] = useState<CapacityRule[] | null>(null);
  const [error, setError] = useState("");

  const load = useCallback(() => {
    getCapacityRules()
      .then(setRules)
      .catch((e) => setError(resolve(e)));
  }, [resolve]);

  useEffect(load, [load]);

  async function create(data: CapacityRuleUpsert) {
    const created = await createCapacityRule(data);
    setRules((prev) => (prev ? [created, ...prev] : [created]));
  }

  async function update(id: string, data: CapacityRuleUpsert) {
    const updated = await updateCapacityRule(id, data);
    setRules((prev) => prev?.map((r) => (r.id === id ? updated : r)) ?? null);
  }

  async function remove(id: string) {
    setRules((prev) => prev?.filter((r) => r.id !== id) ?? null);
    try {
      await deleteCapacityRule(id);
    } catch {
      load();
    }
  }

  return { rules, error, load, create, update, remove };
}
