import { apiFetch } from "./client";
import type { CustomerSearchResult } from "../shared/types";

export function searchCustomers(query: string): Promise<CustomerSearchResult[]> {
  return apiFetch(`/api/customer/search?query=${encodeURIComponent(query)}`);
}
