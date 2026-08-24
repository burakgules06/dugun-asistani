-- WhatsApp teklif akışı yeniden kurgulandı: kişi sayısı artık en başta bir ARALIK olarak
-- sorulup salonu otomatik belirliyor; kapasite kuralları artık "X kişi altı kapalı" eşiği
-- değil, panelden tanımlanan tam kapalı/açık İSTİSNALAR (hall × menu × ay × zaman dilimi).

-- ---------------------------------------------------------------------------
-- wa_conversation: state enum'u degisti (SESSION_SELECT/DAY_TYPE_SELECT/GUEST_COUNT_INPUT
-- kalkti; GUEST_RANGE_SELECT/TIME_SLOT_SELECT eklendi). O state'lerde bekleyen yarim
-- konusmalari once guvenli sekilde sifirla.
-- ---------------------------------------------------------------------------
UPDATE wa_conversation
SET state = 'IDLE', context = '{}'::jsonb
WHERE state IN ('SESSION_SELECT', 'DAY_TYPE_SELECT', 'GUEST_COUNT_INPUT');

ALTER TABLE wa_conversation
    DROP CONSTRAINT wa_conversation_state_check;

ALTER TABLE wa_conversation
    ADD CONSTRAINT wa_conversation_state_check
    CHECK (state IN ('IDLE','NAME_INPUT','MAIN_MENU','GUEST_RANGE_SELECT','HALL_SELECT',
                      'MONTH_SELECT','WEEK_SELECT','MENU_SELECT','TIME_SLOT_SELECT','CONFIRM'));

-- ---------------------------------------------------------------------------
-- capacity_rule: esik (min_guest_count) kaldirildi, day_type -> time_slot (3 degerli,
-- NULL = tum dilimler) oldu. Var olan kurallardaki min_guest_count esikleri artik
-- anlamsiz oldugundan (yeni model tamamen acik/kapali), bu kurallari pasif hale getiriyoruz;
-- işletme panelden yeni istisna kayıtlarını tekrar tanımlayabilir.
UPDATE capacity_rule SET is_active = FALSE;

ALTER TABLE capacity_rule
    DROP COLUMN min_guest_count;

ALTER TABLE capacity_rule
    RENAME COLUMN day_type TO time_slot;

ALTER TABLE capacity_rule
    ALTER COLUMN time_slot DROP NOT NULL,
    ALTER COLUMN time_slot DROP DEFAULT;

-- eski day_type degerleri (WEEKDAY/WEEKEND/ALL) yeni zaman dilimi enum'una birebir
-- karsilik gelmiyor (akşam/gündüz ayrımı yoktu); kurallar zaten pasif hale getirildigi
-- icin kapsamlarini sifirlayip isletmenin panelden yeniden tanimlamasina birakiyoruz.
UPDATE capacity_rule SET time_slot = NULL;

ALTER TABLE capacity_rule
    DROP CONSTRAINT capacity_rule_day_type_check;

ALTER TABLE capacity_rule
    ADD CONSTRAINT capacity_rule_time_slot_check
    CHECK (time_slot IS NULL OR time_slot IN ('WEEKDAY_EVENING', 'WEEKEND_EVENING', 'WEEKEND_DAY'));

-- ---------------------------------------------------------------------------
-- lead: guest_count -> guest_count_min/guest_count_max; preferred_day_type + session ->
-- tek preferred_time_slot alani; capacity_conflict kavrami kaldirildi (yeni modelde
-- listelenen her secenek zaten acik oldugu icin son anda cakisma olusamiyor).
-- ---------------------------------------------------------------------------
ALTER TABLE lead
    ADD COLUMN guest_count_min INT,
    ADD COLUMN guest_count_max INT,
    ADD COLUMN preferred_time_slot VARCHAR(20);

UPDATE lead SET guest_count_min = guest_count;

UPDATE lead SET preferred_time_slot = CASE
    WHEN preferred_day_type = 'WEEKDAY' THEN 'WEEKDAY_EVENING'
    WHEN preferred_day_type = 'WEEKEND' AND session = 'LUNCH' THEN 'WEEKEND_DAY'
    WHEN preferred_day_type = 'WEEKEND' THEN 'WEEKEND_EVENING'
    ELSE NULL
END;

ALTER TABLE lead
    ADD CONSTRAINT lead_preferred_time_slot_check
    CHECK (preferred_time_slot IS NULL OR preferred_time_slot IN ('WEEKDAY_EVENING', 'WEEKEND_EVENING', 'WEEKEND_DAY'));

ALTER TABLE lead
    DROP COLUMN guest_count,
    DROP COLUMN preferred_day_type,
    DROP COLUMN session,
    DROP COLUMN capacity_conflict;
