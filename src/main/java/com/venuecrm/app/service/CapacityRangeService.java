package com.venuecrm.app.service;

import com.venuecrm.app.model.entity.Hall;

import java.util.List;
import java.util.UUID;

public interface CapacityRangeService {

    /**
     * Aktif salonların kapasite aralıklarının (capacityMin/capacityMax) birleşiminden,
     * WhatsApp'ta müşteriye sorulacak kişi-sayısı aralığı seçeneklerini hesaplar.
     * Aynı salon kümesine denk gelen komşu alt aralıklar tek bir seçenekte birleştirilir;
     * bir seçeneğe tek salon denk geliyorsa o salon otomatik seçilebilir, birden fazla
     * salon denk geliyorsa müşteriye o alt küme içinden salon seçtirilir.
     */
    List<GuestRangeOption> computeRanges(UUID tenantId);

    record GuestRangeOption(int start, Integer end, List<Hall> halls) {
        /** id: WhatsApp liste satırının seçim kimliği. */
        public String id() {
            return "GRANGE_" + start + "_" + (end == null ? "OPEN" : end);
        }
    }
}
