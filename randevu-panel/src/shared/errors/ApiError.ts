/**
 * Backend'den gelen standart hata yapısı için özel hata sınıfı.
 *
 * Backend yanıt formatı:
 * { success: false, code: number, message: string, data: null, timestamp: number }
 */
export class ApiError extends Error {
  readonly code: number;
  readonly serverMessage: string;

  constructor(code: number, serverMessage: string) {
    super(serverMessage);
    this.name = "ApiError";
    this.code = code;
    this.serverMessage = serverMessage;
  }

  /** Belirli bir hata koduna eşit mi? */
  is(code: number): boolean {
    return this.code === code;
  }

  /** Verilen kodlardan herhangi biriyle eşleşiyor mu? */
  isAny(...codes: number[]): boolean {
    return codes.includes(this.code);
  }
}
