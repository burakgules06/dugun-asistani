// Backend DTO'larının TypeScript karşılıkları

export type LoginResponse = {
  token: string;
  userId: string;
  fullName: string;
  tenantName: string;
  role: "SUPER_ADMIN" | "ADMIN" | "STAFF";
  subscriptionStatus: string;
  subscriptionUntil: string; // "" olabilir
};

export type Hall = {
  id: string;
  name: string;
  description: string | null;
  capacityMin: number | null;
  capacityMax: number | null;
  sortOrder: number;
  active: boolean;
  /** 1 = günde tek düğün (yalnızca akşam), 2 = günde iki düğün (öğlen + akşam). */
  dailyCapacity: number;
};

export type Menu = {
  id: string;
  name: string;
  description: string | null;
  pricePerPerson: number | null;
  sortOrder: number;
  active: boolean;
  hallIds: string[];
};

/** Müşterinin tercih ettiği zaman dilimi: hafta içi akşam / hafta sonu akşam / hafta sonu gündüz. */
export type TimeSlot = "WEEKDAY_EVENING" | "WEEKEND_EVENING" | "WEEKEND_DAY";

export type CapacityRule = {
  id: string;
  hallId: string | null;
  hallName: string | null;
  menuId: string | null;
  menuName: string | null;
  months: number[];
  timeSlot: TimeSlot | null;
  active: boolean;
  note: string | null;
};

export type LeadStage = "NEW" | "CONTACTED" | "PRICE_GIVEN" | "INVITED" | "WON" | "LOST";
export type LeadMood = "POSITIVE" | "NEGATIVE" | "NEUTRAL" | "CONFUSED";
export type LeadSource = "WHATSAPP_BOT" | "MANUAL";

export type Lead = {
  id: string;
  customerId: string;
  customerName: string;
  customerPhone: string | null;
  hallId: string | null;
  hallName: string | null;
  menuId: string | null;
  menuName: string | null;
  eventDate: string | null; // "2027-08-15" - kesin/onaylanmış gün, işletme panelden belirler
  preferredMonth: string | null; // "2027-08-01" - müşterinin WhatsApp'ta belirttiği ay (ayın ilk günü)
  preferredWeek: number | null; // 1-5 - ayın kaçıncı haftası
  guestCountMin: number | null;
  guestCountMax: number | null; // null = üst sınırı olmayan (açık uçlu) aralık
  preferredTimeSlot: TimeSlot | null;
  source: LeadSource;
  stage: LeadStage;
  mood: LeadMood | null;
  priceGiven: boolean;
  priceAmount: number | null;
  assignedUserId: string | null;
  assignedUserName: string | null;
  noteCount: number;
  createdAt: string;
  updatedAt: string;
};

export type LeadNote = {
  id: string;
  authorName: string;
  body: string;
  createdAt: string;
};

export type CustomerSearchResult = {
  id: string;
  fullName: string | null;
  waNumber: string | null;
};

export type PanelUser = {
  id: string;
  phone: string;
  fullName: string | null;
  role: "SUPER_ADMIN" | "ADMIN" | "STAFF";
  active: boolean;
};

export type TenantSettings = {
  id: string;
  slug: string;
  displayName: string;
  vertical: string;
  waPhoneNumberId: string;
  waDisplayNumber: string;
  timezone: string;
  greetingText: string | null;
  active: boolean;
  subscriptionStatus: string;
  subscriptionUntil: string | null;
  plan: string;
  showPrices: boolean;
};
