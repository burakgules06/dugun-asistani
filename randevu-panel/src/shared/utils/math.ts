// src/utils/math.ts
export function fillPercent(booked: number, total: number): number {
  return total > 0 ? Math.round((booked / total) * 100) : 0;
}