package com.venuecrm.app.whatsapp;

/**
 * Bir WhatsApp mesajinin gonderilecegi hedef. WhatsApp'in username ozelligi nedeniyle
 * musterinin telefon numarasi gizli olabilir; bu durumda Meta'nin Business-Scoped
 * User ID'si (bsuid) ile mesaj gonderilir. En az biri dolu olmalidir.
 */
public record WaTarget(String phone, String bsuid) {

    public WaTarget {
        if ((phone == null || phone.isBlank()) && (bsuid == null || bsuid.isBlank())) {
            throw new IllegalArgumentException("WaTarget: telefon ve bsuid ikisi de bos olamaz");
        }
    }

    public boolean hasPhone() {
        return phone != null && !phone.isBlank();
    }
}
