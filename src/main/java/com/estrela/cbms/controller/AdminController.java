package com.estrela.cbms.controller;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
public class AdminController {

    @Autowired
    private ConfigurableApplicationContext context;

    @PostMapping("/shutdown")
    public ResponseEntity<String> shutdownApplication() {
        log.info("Received request to shut down application.");
        new Thread(() -> {
            try {
                Thread.sleep(100); // Give response a chance to be sent
                SpringApplication.exit(context, () -> 0);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Shutdown thread interrupted", e);
            }
        }).start();
        return ResponseEntity.ok("Application is shutting down...");
    }
}
