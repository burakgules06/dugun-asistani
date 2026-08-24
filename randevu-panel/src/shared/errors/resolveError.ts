import { ApiError } from "./ApiError";
import { ErrorCode } from "./errorCodes";
import type { Translations } from "../i18n/locales/tr";

type TFn = (selector: (l: Translations) => string) => string;

/**
 * Herhangi bir hatayı kullanıcıya gösterilecek string'e çevirir.
 *
 * @param error   - catch bloğundan gelen hata
 * @param t       - useT() hook'undan gelen çeviri fonksiyonu (opsiyonel)
 * @param fallback - Hiçbir kural eşleşmezse kullanılacak metin
 */
export function resolveError(error: unknown, t?: TFn, fallback?: string): string {
  const defaultFallback = fallback ?? (t ? t(l => l.errors.unknown) : "Bir hata oluştu.");

  if (!(error instanceof ApiError)) {
    if (error instanceof Error) return error.message || defaultFallback;
    return defaultFallback;
  }

  if (!t) {
    return builtinMessage(error.code) ?? error.serverMessage ?? defaultFallback;
  }

  switch (error.code) {
    case ErrorCode.VALIDATION_ERROR:
      return t(l => l.errors.validationError);
    case ErrorCode.SUBSCRIPTION_END:
      return t(l => l.errors.subscriptionEnd);
    case ErrorCode.CUSTOMER_NUMBER_ALREADY_EXIST:
      return t(l => l.errors.customerNumberAlreadyExist);
    case ErrorCode.CUSTOMER_NAME_CANT_BE_NULL:
      return t(l => l.errors.customerNameRequired);
    case ErrorCode.HALL_NOT_FOUND:
      return t(l => l.errors.hallNotFound);
    case ErrorCode.HALL_NOT_BELONG_TO_TENANT:
      return t(l => l.errors.hallNotBelongToTenant);
    case ErrorCode.MENU_NOT_FOUND:
      return t(l => l.errors.menuNotFound);
    case ErrorCode.MENU_NOT_BELONG_TO_TENANT:
      return t(l => l.errors.menuNotBelongToTenant);
    case ErrorCode.CAPACITY_RULE_NOT_FOUND:
      return t(l => l.errors.capacityRuleNotFound);
    case ErrorCode.CAPACITY_RULE_NOT_BELONG_TO_TENANT:
      return t(l => l.errors.capacityRuleNotBelongToTenant);
    case ErrorCode.LEAD_NOT_FOUND:
      return t(l => l.errors.leadNotFound);
    case ErrorCode.LEAD_NOT_BELONG_TO_TENANT:
      return t(l => l.errors.leadNotBelongToTenant);
    case ErrorCode.TENANT_NOT_FOUND:
      return t(l => l.errors.tenantNotFound);
    case ErrorCode.PANEL_USER_NOT_FOUND:
      return t(l => l.errors.panelUserNotFound);
    case ErrorCode.PANEL_USER_PHONE_ALREADY_EXIST:
      return t(l => l.errors.panelUserPhoneAlreadyExist);
    default:
      return t(l => l.errors.unknown);
  }
}

const messages: Record<number, string> = {
  [ErrorCode.VALIDATION_ERROR]: "Geçersiz veri. Lütfen girişlerinizi kontrol edin.",
  [ErrorCode.SUBSCRIPTION_END]: "Aboneliğiniz sona erdi. Lütfen yenileyiniz.",
  [ErrorCode.CUSTOMER_NUMBER_ALREADY_EXIST]: "Bu telefon numarası zaten kayıtlı.",
  [ErrorCode.CUSTOMER_NAME_CANT_BE_NULL]: "Müşteri adı boş bırakılamaz.",
  [ErrorCode.HALL_NOT_FOUND]: "Salon bulunamadı.",
  [ErrorCode.HALL_NOT_BELONG_TO_TENANT]: "Bu salon işletmenize ait değil.",
  [ErrorCode.MENU_NOT_FOUND]: "Menü bulunamadı.",
  [ErrorCode.MENU_NOT_BELONG_TO_TENANT]: "Bu menü işletmenize ait değil.",
  [ErrorCode.CAPACITY_RULE_NOT_FOUND]: "Kapasite kuralı bulunamadı.",
  [ErrorCode.CAPACITY_RULE_NOT_BELONG_TO_TENANT]: "Bu kural işletmenize ait değil.",
  [ErrorCode.LEAD_NOT_FOUND]: "Talep bulunamadı.",
  [ErrorCode.LEAD_NOT_BELONG_TO_TENANT]: "Bu talep işletmenize ait değil.",
  [ErrorCode.TENANT_NOT_FOUND]: "İşletme bulunamadı.",
  [ErrorCode.PANEL_USER_NOT_FOUND]: "Kullanıcı bulunamadı.",
  [ErrorCode.PANEL_USER_PHONE_ALREADY_EXIST]: "Bu telefon numarasıyla kayıtlı bir kullanıcı zaten var.",
  [ErrorCode.UNKNOWN_ERROR]: "Bilinmeyen bir hata oluştu.",
};

function builtinMessage(code: number): string | undefined {
  return messages[code];
}
