package dev.esanchez.timely.backend.module.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String testConnection() {
        return "Backend connected successfully";
    }
}