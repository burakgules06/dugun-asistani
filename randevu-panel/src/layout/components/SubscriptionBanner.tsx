import { Banner } from "../../shared/components";

// Only renders inside the last 7 days of a subscription (or once it has lapsed).
export function SubscriptionBanner({ subscriptionUntil }: { subscriptionUntil: string }) {
  const until = new Date(subscriptionUntil + "T00:00:00");
  const daysLeft = Math.ceil((until.getTime() - Date.now()) / 86400000);
  if (daysLeft > 7) return null;

  const expired = daysLeft < 0;

  return (
    <div className="px-5 pt-2 box-border">
      <Banner variant={expired ? "error" : "warning"} icon={false}>
        {expired ? "Aboneliğiniz sona erdi. Lütfen yenileyiniz." : `Aboneliğiniz ${daysLeft} gün sonra sona eriyor.`}
      </Banner>
    </div>
  );
}
