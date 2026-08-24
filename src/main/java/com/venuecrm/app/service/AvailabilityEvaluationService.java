package com.venuecrm.app.service;

import com.venuecrm.app.model.enums.TimeSlot;

import java.util.UUID;

public interface AvailabilityEvaluationService {
    /**
     * Verilen salon/menü/ay/zaman dilimi kombinasyonu panelden tanımlı bir istisna
     * tarafından kapatılmış mı diye kontrol eder. Müşteri kesin bir tarih vermediği için
     * (WhatsApp botu ay + zaman dilimi tercihi topluyor) değerlendirme doğrudan ay + zaman
     * dilimi üzerinden yapılır.
     * timeSlot=null ise (henüz zaman dilimi seçilmemiş, menü listesi filtrelenirken) yalnızca
     * tüm dilimleri kapsayan (timeSlot=null) istisnalar dikkate alınır; belirli bir dilim
     * verilirse hem o dilime özel hem tüm dilimleri kapsayan istisnalar dikkate alınır.
     */
    boolean isClosed(UUID tenantId, UUID hallId, UUID menuId, Integer month, TimeSlot timeSlot);
}
