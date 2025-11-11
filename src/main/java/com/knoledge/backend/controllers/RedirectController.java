package com.knoledge.backend.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class RedirectController {

    private final String clientUrl;

    public RedirectController(@Value("${CLIENT_ORIGIN:http://localhost:5173}") String clientUrl) {
        this.clientUrl = clientUrl.endsWith("/") ? clientUrl : clientUrl + "/";
    }

    @GetMapping("/")
    public RedirectView redirectToFrontend() {
        return new RedirectView(clientUrl);
    }
}
