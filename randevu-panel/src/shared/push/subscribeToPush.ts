import { deletePushSubscription, getPushPublicKey, savePushSubscription } from "../../api/push";

export type PushSupportStatus = "unsupported" | "unsubscribed" | "subscribed";

export function isPushSupported(): boolean {
  return "serviceWorker" in navigator && "PushManager" in window && "Notification" in window;
}

export async function getPushStatus(): Promise<PushSupportStatus> {
  if (!isPushSupported()) return "unsupported";

  const registration = await navigator.serviceWorker.ready.catch(() => null);
  if (!registration) return "unsubscribed";

  const existing = await registration.pushManager.getSubscription().catch(() => null);
  return existing ? "subscribed" : "unsubscribed";
}

export async function subscribeToPush(): Promise<boolean> {
  if (!isPushSupported()) return false;

  const permission = await Notification.requestPermission();
  if (permission !== "granted") return false;

  const registration = await navigator.serviceWorker.ready;

  let subscription = await registration.pushManager.getSubscription();
  if (!subscription) {
    const { publicKey } = await getPushPublicKey();
    if (!publicKey) return false;

    subscription = await registration.pushManager.subscribe({
      userVisibleOnly: true,
      applicationServerKey: urlBase64ToUint8Array(publicKey),
    });
  }

  await savePushSubscription(subscription.toJSON() as PushSubscriptionJSON);
  return true;
}

export async function unsubscribeFromPush(): Promise<boolean> {
  if (!isPushSupported()) return false;

  const registration = await navigator.serviceWorker.ready.catch(() => null);
  if (!registration) return true;

  const subscription = await registration.pushManager.getSubscription();
  if (!subscription) return true;

  const endpoint = subscription.endpoint;
  await subscription.unsubscribe();
  await deletePushSubscription(endpoint).catch(() => {});
  return true;
}

function urlBase64ToUint8Array(base64Url: string): BufferSource {
  const padding = "=".repeat((4 - (base64Url.length % 4)) % 4);
  const base64 = (base64Url + padding).replace(/-/g, "+").replace(/_/g, "/");
  const rawData = atob(base64);
  const bytes = new Uint8Array(new ArrayBuffer(rawData.length));
  for (let i = 0; i < rawData.length; i++) {
    bytes[i] = rawData.charCodeAt(i);
  }
  return bytes;
}
