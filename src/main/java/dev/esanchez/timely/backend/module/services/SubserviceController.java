package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.services.dto.request.CreateSubserviceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.SubserviceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/service/subservice")
@RequiredArgsConstructor
public class SubserviceController {

    @Autowired
    private SubserviceManagementService subserviceManagementService;

    @PostMapping
    public ResponseEntity<SubserviceResponse> createSubservice(@RequestBody CreateSubserviceRequest createSubserviceRequest) {
        SubserviceResponse response = subserviceManagementService.createSubservice(createSubserviceRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{service_id}")
    public ResponseEntity<List<SubserviceResponse>> getAllSubservices(@PathVariable long service_id) {
        List<SubserviceResponse> responses = subserviceManagementService.getAllSubservices(service_id);
        return ResponseEntity.ok(responses);
    }
}
