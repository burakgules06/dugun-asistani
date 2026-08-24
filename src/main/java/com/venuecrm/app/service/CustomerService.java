package com.venuecrm.app.service;

import com.venuecrm.app.model.entity.Customer;
import com.venuecrm.app.model.entity.Tenant;
import com.venuecrm.app.model.response.CustomerSearchResponse;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    Customer findOrCreate(Tenant tenant, String waNumber, String waUserId, String profileName);
    List<CustomerSearchResponse> search(UUID tenantId, String query);
}
