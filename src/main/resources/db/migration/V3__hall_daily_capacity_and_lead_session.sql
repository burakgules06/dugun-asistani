-- Salonun günde kaç düğün alabildiği (1 = yalnızca akşam, 2 = öğlen + akşam)
-- ve bir talebin hangi oturuma (öğlen/akşam) ait olduğu.

ALTER TABLE hall
    ADD COLUMN daily_capacity SMALLINT NOT NULL DEFAULT 1 CHECK (daily_capacity IN (1, 2));

ALTER TABLE lead
    ADD COLUMN session VARCHAR(10) CHECK (session IN ('LUNCH', 'EVENING'));

ALTER TABLE wa_conversation
    DROP CONSTRAINT wa_conversation_state_check;

ALTER TABLE wa_conversation
    ADD CONSTRAINT wa_conversation_state_check
    CHECK (state IN ('IDLE','NAME_INPUT','MAIN_MENU','HALL_SELECT','SESSION_SELECT','MENU_SELECT',
                      'DATE_INPUT','GUEST_COUNT_INPUT','CONFIRM'));
