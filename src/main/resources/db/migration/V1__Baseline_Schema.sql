-- ============================================================================
-- DÜĞÜN SALONU SATIŞ PANELİ - Baseline Şema
-- Multi-tenant (birden fazla düğün salonu işletmesi aynı paneli kullanabilir)
-- PostgreSQL 15+
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS pgcrypto;     -- gen_random_uuid()

-- ----------------------------------------------------------------------------
-- TENANT: hizmet veren işletme (multi-tenancy kökü)
-- ----------------------------------------------------------------------------
CREATE TABLE tenant (
    id                   UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    slug                 VARCHAR(60)  NOT NULL UNIQUE,
    display_name         VARCHAR(120) NOT NULL,
    vertical             VARCHAR(40)  NOT NULL DEFAULT 'WEDDING_VENUE',
    wa_phone_number_id   VARCHAR(40)  NOT NULL UNIQUE,
    wa_display_number    VARCHAR(20)  NOT NULL,
    wa_access_token      TEXT,
    timezone             VARCHAR(40)  NOT NULL DEFAULT 'Europe/Istanbul',
    greeting_text        TEXT,
    is_active            BOOLEAN      NOT NULL DEFAULT TRUE,
    subscription_status  VARCHAR(20)  NOT NULL DEFAULT 'TRIAL'
                         CHECK (subscription_status IN ('TRIAL', 'ACTIVE', 'PAST_DUE', 'CANCELLED')),
    subscription_until   DATE,
    plan                 VARCHAR(20)  NOT NULL DEFAULT 'BASIC',
    show_prices          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at           TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at           TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ----------------------------------------------------------------------------
-- PANEL_USER: İşletme sahipleri ve çalışanların panele giriş tablosu
-- ----------------------------------------------------------------------------
CREATE TABLE panel_user (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID REFERENCES tenant(id), -- NULLABLE (Süper Adminler için)
    phone         VARCHAR(20)  NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    full_name     VARCHAR(120),
    role          VARCHAR(20)  NOT NULL DEFAULT 'STAFF'
                  CHECK (role IN ('SUPER_ADMIN', 'ADMIN', 'STAFF')),
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ----------------------------------------------------------------------------
-- HALL: bir işletmenin fiziksel salonları (aynı yapı içinde birden fazla olabilir)
-- ----------------------------------------------------------------------------
CREATE TABLE hall (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id     UUID NOT NULL REFERENCES tenant(id),
    name          VARCHAR(100) NOT NULL,
    description   VARCHAR(500),
    capacity_min  INT,
    capacity_max  INT,
    sort_order    SMALLINT NOT NULL DEFAULT 0,
    is_active     BOOLEAN  NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, name)
);

-- ----------------------------------------------------------------------------
-- MENU: menü paketleri (kişi başı fiyat)
-- ----------------------------------------------------------------------------
CREATE TABLE menu (
    id                UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id         UUID NOT NULL REFERENCES tenant(id),
    name              VARCHAR(100) NOT NULL,
    description       VARCHAR(500),
    price_per_person  NUMERIC(10,2),
    sort_order        SMALLINT NOT NULL DEFAULT 0,
    is_active         BOOLEAN  NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, name)
);

-- Bir menünün hangi salon(lar)da sunulduğu (n-n). Bir menü için hiç satır yoksa
-- tüm salonlarda geçerli sayılır (bkz. BookingMessageServiceImpl.isMenuAvailableForHall).
CREATE TABLE hall_menu (
    id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hall_id  UUID NOT NULL REFERENCES hall(id) ON DELETE CASCADE,
    menu_id  UUID NOT NULL REFERENCES menu(id) ON DELETE CASCADE,
    UNIQUE (hall_id, menu_id)
);

-- ----------------------------------------------------------------------------
-- CAPACITY_RULE: kişi sayısı eşiğine göre belirli ay(lar) + haftaiçi/haftasonu
-- kombinasyonunda bir salon/menünün kapatılmasını tanımlar
-- ----------------------------------------------------------------------------
CREATE TABLE capacity_rule (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id        UUID NOT NULL REFERENCES tenant(id),
    hall_id          UUID REFERENCES hall(id),   -- NULL => tüm salonlar
    menu_id          UUID REFERENCES menu(id),   -- NULL => tüm menüler
    months           SMALLINT[],                 -- NULL/boş => tüm aylar
    day_type         VARCHAR(10) NOT NULL DEFAULT 'ALL'
                     CHECK (day_type IN ('WEEKDAY', 'WEEKEND', 'ALL')),
    min_guest_count  INT NOT NULL DEFAULT 0,
    is_active        BOOLEAN NOT NULL DEFAULT TRUE,
    note             VARCHAR(255),
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at       TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_caprule_tenant ON capacity_rule (tenant_id, is_active);

-- ----------------------------------------------------------------------------
-- CUSTOMER: hizmet alan (WhatsApp numarası kimliktir)
-- ----------------------------------------------------------------------------
CREATE TABLE customer (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id   UUID NOT NULL REFERENCES tenant(id),
    wa_number   VARCHAR(20),
    wa_user_id  VARCHAR(64),
    full_name   VARCHAR(120),
    is_blocked  BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, wa_number)
);
CREATE UNIQUE INDEX idx_customer_wa_user_id ON customer (tenant_id, wa_user_id) WHERE wa_user_id IS NOT NULL;

-- ----------------------------------------------------------------------------
-- LEAD: satış talebi kartı (WhatsApp botundan ya da elle oluşturulan teklif talebi)
-- ----------------------------------------------------------------------------
CREATE TABLE lead (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id          UUID NOT NULL REFERENCES tenant(id),
    customer_id        UUID NOT NULL REFERENCES customer(id),
    hall_id            UUID REFERENCES hall(id),
    menu_id            UUID REFERENCES menu(id),
    event_date         DATE,
    guest_count        INT,
    source             VARCHAR(20) NOT NULL DEFAULT 'MANUAL'
                       CHECK (source IN ('WHATSAPP_BOT', 'MANUAL')),
    stage              VARCHAR(20) NOT NULL DEFAULT 'NEW'
                       CHECK (stage IN ('NEW', 'CONTACTED', 'PRICE_GIVEN', 'INVITED', 'WON', 'LOST')),
    mood               VARCHAR(20)
                       CHECK (mood IN ('POSITIVE', 'NEGATIVE', 'NEUTRAL', 'CONFUSED')),
    price_given        BOOLEAN NOT NULL DEFAULT FALSE,
    price_amount       NUMERIC(10,2),
    capacity_conflict  BOOLEAN NOT NULL DEFAULT FALSE,
    assigned_user_id   UUID REFERENCES panel_user(id),
    created_at         TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_lead_tenant ON lead (tenant_id, created_at DESC);
CREATE INDEX idx_lead_stage ON lead (tenant_id, stage);

-- ----------------------------------------------------------------------------
-- LEAD_NOTE: bir talebe bağlı not geçmişi (personel + sistem notları)
-- ----------------------------------------------------------------------------
CREATE TABLE lead_note (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lead_id     UUID NOT NULL REFERENCES lead(id) ON DELETE CASCADE,
    author_id   UUID REFERENCES panel_user(id),
    body        TEXT NOT NULL,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_leadnote_lead ON lead_note (lead_id, created_at DESC);

-- ----------------------------------------------------------------------------
-- WA_CONVERSATION: bot state machine'in kalıcı durumu
-- ----------------------------------------------------------------------------
CREATE TABLE wa_conversation (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id       UUID NOT NULL REFERENCES tenant(id),
    customer_id     UUID NOT NULL REFERENCES customer(id),
    state           VARCHAR(20) NOT NULL DEFAULT 'IDLE'
                    CHECK (state IN ('IDLE','NAME_INPUT','MAIN_MENU','HALL_SELECT','MENU_SELECT',
                                     'DATE_INPUT','GUEST_COUNT_INPUT','CONFIRM')),
    context         JSONB NOT NULL DEFAULT '{}'::jsonb,
    last_inbound_at TIMESTAMPTZ,
    created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (tenant_id, customer_id)
);

-- ----------------------------------------------------------------------------
-- WA_MESSAGE_LOG: gelen/giden tüm mesajlar
-- ----------------------------------------------------------------------------
CREATE TABLE wa_message_log (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id     UUID NOT NULL REFERENCES tenant(id),
    customer_id   UUID REFERENCES customer(id),
    direction     VARCHAR(3) NOT NULL CHECK (direction IN ('IN','OUT')),
    wa_message_id VARCHAR(80) UNIQUE,
    payload       JSONB NOT NULL,
    created_at    TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE INDEX idx_msglog_conv ON wa_message_log (tenant_id, customer_id, created_at);

-- ----------------------------------------------------------------------------
-- MESSAGE_OUTBOX: transactional outbox (gecikmeli/takip mesajları için)
-- ----------------------------------------------------------------------------
CREATE TABLE message_outbox (
    id             BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    tenant_id      UUID NOT NULL REFERENCES tenant(id),
    customer_id    UUID NOT NULL REFERENCES customer(id),
    msg_type       VARCHAR(30) NOT NULL
                   CHECK (msg_type IN ('SESSION_REPLY','TEMPLATE_FOLLOWUP')),
    payload        JSONB NOT NULL,
    scheduled_for  TIMESTAMPTZ NOT NULL DEFAULT now(),
    status         VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                   CHECK (status IN ('PENDING','SENT','FAILED','DEAD')),
    attempt_count  SMALLINT NOT NULL DEFAULT 0,
    last_error     TEXT,
    lead_id        UUID REFERENCES lead(id),
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    sent_at        TIMESTAMPTZ
);
CREATE INDEX idx_outbox_poll ON message_outbox (scheduled_for) WHERE status = 'PENDING';

-- ----------------------------------------------------------------------------
-- PUSH_SUBSCRIPTION: panel kullanıcılarının web push abonelikleri
-- ----------------------------------------------------------------------------
CREATE TABLE push_subscription (
    id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    panel_user_id  UUID NOT NULL REFERENCES panel_user(id) ON DELETE CASCADE,
    endpoint       VARCHAR(500) NOT NULL UNIQUE,
    p256dh_key     VARCHAR(200) NOT NULL,
    auth_key       VARCHAR(100) NOT NULL,
    created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
