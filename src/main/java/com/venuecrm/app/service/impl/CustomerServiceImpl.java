package com.venuecrm.app.service.impl;

import com.venuecrm.app.model.entity.Customer;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.response.CustomerSearchResponse;
import com.venuecrm.app.repository.CustomerRepository;
import com.venuecrm.app.service.CustomerService;
import com.venuecrm.app.util.MessageUtil;
import com.venuecrm.app.util.PhoneUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private static final int SEARCH_MIN_LENGTH = 2;
    private static final int SEARCH_RESULT_LIMIT = 10;

    private final CustomerRepository customerRepository;

    @Override
    @Transactional
    public Customer findOrCreate(Tenant tenant, String waNumber, String waUserId, String profileName) {
        String normalizedPhone = PhoneUtil.normalize(waNumber);
        boolean hasPhone = normalizedPhone != null && !normalizedPhone.isBlank();
        boolean hasBsuid = waUserId != null && !waUserId.isBlank();

        if (!hasPhone && !hasBsuid) {
            throw new IllegalArgumentException(
                    "Musteri kimligi cozumlenemedi (numara veya BSUID yok): " + waNumber);
        }

        if (hasPhone) {
            Customer existing = customerRepository
                    .findByTenantIdAndWaNumber(tenant.getId(), normalizedPhone)
                    .orElse(null);
            if (existing != null) {
                if (hasBsuid && !waUserId.equals(existing.getWaUserId())) {
                    existing.setWaUserId(waUserId);
                    customerRepository.save(existing);
                }
                return existing;
            }
        } else {
            Customer existing = customerRepository
                    .findByTenantIdAndWaUserId(tenant.getId(), waUserId)
                    .orElse(null);
            if (existing != null) {
                return existing;
            }
        }

        Customer c = new Customer();
        c.setTenant(tenant);
        c.setWaNumber(hasPhone ? normalizedPhone : null);
        c.setWaUserId(hasBsuid ? waUserId : null);
        c.setFullName(MessageUtil.cleanName(profileName));
        return customerRepository.save(c);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerSearchResponse> search(UUID tenantId, String query) {
        String trimmed = query == null ? "" : query.trim();
        if (trimmed.length() < SEARCH_MIN_LENGTH) {
            return Collections.emptyList();
        }
        return customerRepository
                .search(tenantId, trimmed, PageRequest.of(0, SEARCH_RESULT_LIMIT))
                .stream()
                .map(c -> new CustomerSearchResponse(c.getId().toString(), c.getFullName(), c.getWaNumber()))
                .toList();
    }
}
