-- ============================================================================
-- DEV SEED DATA - Demo düğün salonu işletmesi
-- ============================================================================

-- 1) Demo İşletme (Tenant)
INSERT INTO tenant (id, slug, display_name, vertical,
                    wa_phone_number_id, wa_display_number,
                    wa_access_token, subscription_status, subscription_until, plan)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    'demo-dugun-salonu',
    'Demo Düğün Salonu',
    'WEDDING_VENUE',
    '1293863200467029',
    '905496142671',
    NULL,                      -- token: prod'da UPDATE ile girilecek
    'ACTIVE',
    '2027-12-31',
    'BASIC'
);

-- 2) Salonlar
INSERT INTO hall (id, tenant_id, name, description, capacity_min, capacity_max, sort_order)
VALUES
  ('22222222-2222-2222-2222-222222222221', '11111111-1111-1111-1111-111111111111',
   'Elmas Salon', 'Ana bina, geniş balo salonu', 100, 500, 0),
  ('22222222-2222-2222-2222-222222222222', '11111111-1111-1111-1111-111111111111',
   'İnci Salon', 'Aynı bina içinde daha butik salon', 50, 200, 1);

-- 3) Menüler
INSERT INTO menu (id, tenant_id, name, description, price_per_person, sort_order)
VALUES
  ('33333333-3333-3333-3333-333333333301', '11111111-1111-1111-1111-111111111111',
   'Klasik Menü', 'Standart açık büfe menü', 800.00, 0),
  ('33333333-3333-3333-3333-333333333302', '11111111-1111-1111-1111-111111111111',
   'Premium Menü', 'Zengin içerikli açık büfe + canlı istasyonlar', 1200.00, 1),
  ('33333333-3333-3333-3333-333333333303', '11111111-1111-1111-1111-111111111111',
   'VIP Menü', 'Servis usulü, özel şef menüsü', 1800.00, 2);

-- 4) Hangi menü hangi salonda sunulur (İnci Salon'da VIP menü yok - alan kısıtı)
INSERT INTO hall_menu (hall_id, menu_id) VALUES
  ('22222222-2222-2222-2222-222222222221', '33333333-3333-3333-3333-333333333301'),
  ('22222222-2222-2222-2222-222222222221', '33333333-3333-3333-3333-333333333302'),
  ('22222222-2222-2222-2222-222222222221', '33333333-3333-3333-3333-333333333303'),
  ('22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333301'),
  ('22222222-2222-2222-2222-222222222222', '33333333-3333-3333-3333-333333333302');

-- 5) Örnek kapasite kuralları
--    a) Kasım-Şubat (kış ayları) arasında, haftaiçi 150 kişinin altındaki talepler kapalı
INSERT INTO capacity_rule (tenant_id, hall_id, menu_id, months, day_type, min_guest_count, note)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    NULL, NULL,
    ARRAY[11,12,1,2]::smallint[],
    'WEEKDAY',
    150,
    'Kış aylarında haftaiçi düşük katılımlı organizasyon kabul edilmiyor'
);
--    b) İnci Salon, hafta sonu her ay 80 kişinin altı kapalı
INSERT INTO capacity_rule (tenant_id, hall_id, menu_id, months, day_type, min_guest_count, note)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    '22222222-2222-2222-2222-222222222222', NULL,
    NULL,
    'WEEKEND',
    80,
    'İnci Salon hafta sonu minimum kişi sayısı'
);

-- 6) PANEL KULLANICILARI
-- Ayşe Yönetici (ADMIN) | Telefon: 905301390726 | Şifre: demo1234
INSERT INTO panel_user (tenant_id, phone, password_hash, full_name, role)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    '905301390726',
    '$2a$10$kzVAnwupUhoKpDnD0T32I..S5nFy.fDnccZgTTj.I2E4ceNygNyS6',
    'Ayşe Yönetici',
    'ADMIN'
);

-- Can Satış Temsilcisi (STAFF) | Telefon: 905424333182 | Şifre: demo1234
INSERT INTO panel_user (tenant_id, phone, password_hash, full_name, role)
VALUES (
    '11111111-1111-1111-1111-111111111111',
    '905424333182',
    '$2a$10$kzVAnwupUhoKpDnD0T32I..S5nFy.fDnccZgTTj.I2E4ceNygNyS6',
    'Can Satış Temsilcisi',
    'STAFF'
);
