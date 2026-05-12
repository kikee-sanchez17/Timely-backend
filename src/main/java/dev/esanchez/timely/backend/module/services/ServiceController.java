package dev.esanchez.timely.backend.module.services;

import dev.esanchez.timely.backend.module.services.dto.request.CreateServiceRequest;
import dev.esanchez.timely.backend.module.services.dto.response.ServiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/service")
@RequiredArgsConstructor
public class ServiceController {

    private final ServiceManagementService serviceManagementService;

    @PostMapping("/create")
    public void createService (@RequestBody @Valid CreateServiceRequest createServiceRequest){
        serviceManagementService.createService(createServiceRequest);
    }

    @GetMapping("/getAllServices")
    public ResponseEntity<List<ServiceResponse>> getAllService(){
        return ResponseEntity.ok(serviceManagementService.getAllServices());
    }


}
