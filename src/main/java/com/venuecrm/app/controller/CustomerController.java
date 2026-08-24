package com.venuecrm.app.controller;

import com.venuecrm.app.model.response.CustomerSearchResponse;
import com.venuecrm.app.security.PanelPrincipal;
import com.venuecrm.app.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping("/search")
    public ResponseEntity<List<CustomerSearchResponse>> search(
            @AuthenticationPrincipal PanelPrincipal principal,
            @RequestParam String query) {
        return ResponseEntity.ok(customerService.search(principal.tenantId(), query));
    }
}
