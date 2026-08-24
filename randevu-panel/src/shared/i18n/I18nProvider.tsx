import { createContext, useCallback, useContext, useState, type ReactNode } from "react";
import { tr, type Translations } from "./locales/tr";
import { en } from "./locales/en";

export type Locale = "tr" | "en";

const LOCALES: Record<Locale, Translations> = { tr, en };
const STORAGE_KEY = "randevu_locale";

function getInitialLocale(): Locale {
  const stored = localStorage.getItem(STORAGE_KEY);
  return stored === "en" ? "en" : "tr";
}

// ─── Interpolation helper ────────────────────────────────────────────────────
function interpolate(str: string, vars?: Record<string, string | number>): string {
  if (!vars) return str;
  return Object.entries(vars).reduce(
    (acc, [key, val]) => acc.replace(`{{${key}}}`, String(val)),
    str,
  );
}

// ─── Context ─────────────────────────────────────────────────────────────────
interface I18nContextValue {
  locale: Locale;
  setLocale: (l: Locale) => void;
  translations: Translations;
}

const I18nContext = createContext<I18nContextValue | null>(null);

// ─── Provider ────────────────────────────────────────────────────────────────
export function I18nProvider({ children }: { children: ReactNode }) {
  const [locale, setLocaleState] = useState<Locale>(getInitialLocale);

  const setLocale = (l: Locale) => {
    localStorage.setItem(STORAGE_KEY, l);
    setLocaleState(l);
  };

  return (
    <I18nContext.Provider value={{ locale, setLocale, translations: LOCALES[locale] }}>
      {children}
    </I18nContext.Provider>
  );
}

// ─── Hook ────────────────────────────────────────────────────────────────────
type Selector<T> = (t: Translations) => T;

export function useT() {
  const ctx = useContext(I18nContext);
  if (!ctx) throw new Error("useT must be used inside I18nProvider");

  const { locale, setLocale, translations } = ctx;

  /** Type-safe translation lookup with optional {{variable}} interpolation */
  const t = useCallback(
    <T,>(selector: Selector<T>, vars?: Record<string, string | number>): T => {
      const value = selector(translations);
      if (typeof value === "string" && vars) {
        return interpolate(value, vars) as T;
      }
      return value;
    },
    [translations],
  );

  return { t, locale, setLocale };
}
