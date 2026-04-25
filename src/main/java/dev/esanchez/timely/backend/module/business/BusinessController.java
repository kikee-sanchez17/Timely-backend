package dev.esanchez.timely.backend.module.business;

import dev.esanchez.timely.backend.core.security.CustomUserDetails;
import dev.esanchez.timely.backend.module.business.dto.request.CreateBusinessRequest;
import dev.esanchez.timely.backend.module.business.dto.response.CreateBusinessResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/business")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;

    @PostMapping
    public ResponseEntity<CreateBusinessResponse> createBusiness(
            @Valid
            @RequestBody CreateBusinessRequest request,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        String newToken = businessService.createBusiness(request, customUserDetails.getUsername());

        return ResponseEntity.ok(new CreateBusinessResponse(newToken));

    }

}
