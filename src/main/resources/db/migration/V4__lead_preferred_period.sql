-- WhatsApp botu artık kesin tarih değil, müşterinin tercih ettiği yaklaşık dönemi
-- (ay + ayın kaçıncı haftası + hafta içi/hafta sonu) topluyor. Kesin tarihi (event_date)
-- işletme, müşteriyle görüştükten sonra panelden belirler.

ALTER TABLE lead
    ADD COLUMN preferred_month DATE,
    ADD COLUMN preferred_week SMALLINT CHECK (preferred_week BETWEEN 1 AND 5),
    ADD COLUMN preferred_day_type VARCHAR(10) CHECK (preferred_day_type IN ('WEEKDAY', 'WEEKEND', 'ALL'));

ALTER TABLE wa_conversation
    DROP CONSTRAINT wa_conversation_state_check;

ALTER TABLE wa_conversation
    ADD CONSTRAINT wa_conversation_state_check
    CHECK (state IN ('IDLE','NAME_INPUT','MAIN_MENU','HALL_SELECT','SESSION_SELECT','MENU_SELECT',
                      'MONTH_SELECT','WEEK_SELECT','DAY_TYPE_SELECT','GUEST_COUNT_INPUT','CONFIRM'));
