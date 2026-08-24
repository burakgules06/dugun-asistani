-- Karşılama akışından ara "Teklif Al / Salonlarımız" menüsü kaldırıldı; bot artık
-- doğrudan kişi sayısı aralığı sorusuyla başlıyor. MAIN_MENU state'i artık kullanılmıyor.

UPDATE wa_conversation
SET state = 'IDLE', context = '{}'::jsonb
WHERE state = 'MAIN_MENU';

ALTER TABLE wa_conversation
    DROP CONSTRAINT wa_conversation_state_check;

ALTER TABLE wa_conversation
    ADD CONSTRAINT wa_conversation_state_check
    CHECK (state IN ('IDLE','NAME_INPUT','GUEST_RANGE_SELECT','HALL_SELECT',
                      'MONTH_SELECT','WEEK_SELECT','MENU_SELECT','TIME_SLOT_SELECT','CONFIRM'));
