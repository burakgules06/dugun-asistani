/**
 * Backend ErrorMessageConstant karşılıkları.
 * Yeni kod eklemek için buraya sabit ekleyip
 * resolveError.ts içindeki switch'e karşılık mesajı girin.
 */
export const ErrorCode = {
  NO_ERROR: -1,
  UNKNOWN_ERROR: 999,
  VALIDATION_ERROR: 1000,
  SUBSCRIPTION_END: 1001,
  CUSTOMER_NUMBER_ALREADY_EXIST: 1002,
  CUSTOMER_NAME_CANT_BE_NULL: 1003,
  HALL_NOT_FOUND: 1004,
  HALL_NOT_BELONG_TO_TENANT: 1005,
  MENU_NOT_FOUND: 1006,
  MENU_NOT_BELONG_TO_TENANT: 1007,
  CAPACITY_RULE_NOT_FOUND: 1008,
  CAPACITY_RULE_NOT_BELONG_TO_TENANT: 1009,
  LEAD_NOT_FOUND: 1010,
  LEAD_NOT_BELONG_TO_TENANT: 1011,
  LEAD_NOTE_NOT_FOUND: 1012,
  TENANT_NOT_FOUND: 1013,
  PANEL_USER_NOT_FOUND: 1014,
  PANEL_USER_PHONE_ALREADY_EXIST: 1015,
} as const;

export type ErrorCodeValue = typeof ErrorCode[keyof typeof ErrorCode];
