package com.knoledge.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RedirectController {

    @GetMapping("/")
    public RedirectView redirectToFrontend() {
        // Redirige al servidor del frontend (Vite o el que uses)
        return new RedirectView("http://localhost:5173/");
    }
}
