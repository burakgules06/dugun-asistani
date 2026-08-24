-- V5, day_type (VARCHAR(10)) sutununu time_slot olarak yeniden adlandirmisti ama
-- kolonun genisligini degistirmemisti. Yeni TimeSlot degerleri (WEEKDAY_EVENING,
-- WEEKEND_EVENING, WEEKEND_DAY) 10 karakterden uzun oldugu icin insert basarisiz oluyordu.
ALTER TABLE capacity_rule
    ALTER COLUMN time_slot TYPE VARCHAR(20);
