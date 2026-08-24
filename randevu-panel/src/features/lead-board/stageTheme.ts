import type { Tone } from "../../shared/theme/tones";
import type { LeadMood, LeadStage } from "../../shared/types";

export const STAGE_TONE: Record<LeadStage, Tone> = {
  NEW: "blue",
  CONTACTED: "purple",
  PRICE_GIVEN: "amber",
  INVITED: "gray",
  WON: "green",
  LOST: "red",
};

export const MOOD_TONE: Record<LeadMood, Tone> = {
  POSITIVE: "green",
  NEGATIVE: "red",
  NEUTRAL: "gray",
  CONFUSED: "amber",
};
