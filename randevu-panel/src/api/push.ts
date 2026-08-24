import { apiFetch } from "./client";

export function getPushPublicKey(): Promise<{ publicKey: string }> {
  return apiFetch("/api/push/public-key");
}

export function savePushSubscription(sub: PushSubscriptionJSON): Promise<void> {
  return apiFetch("/api/push/subscribe", {
    method: "POST",
    body: JSON.stringify(sub),
  });
}

export function deletePushSubscription(endpoint: string): Promise<void> {
  return apiFetch("/api/push/subscribe", {
    method: "DELETE",
    body: JSON.stringify({ endpoint }),
  });
}
