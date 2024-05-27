package com.shein.parser.controller;

import com.shein.parser.model.ApiRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Controller
public class ApiController {

    private final WebClient webClient;

    public ApiController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @GetMapping("/api")
    public String showForm(Model model) {
        model.addAttribute("apiRequest", new ApiRequest());
        return "api_form";
    }

    @PostMapping("/test-api")
    public String testApi(@ModelAttribute ApiRequest apiRequest, Model model) {
        try {
            WebClient.RequestHeadersSpec<?> request = webClient.get().uri(apiRequest.getUrl());
            if (apiRequest.getHost() != null && !apiRequest.getHost().isEmpty()) {
                request.header("x-rapidapi-host", apiRequest.getHost());
            }
            if (apiRequest.getKey() != null && !apiRequest.getKey().isEmpty()) {
                request.header("x-rapidapi-key", apiRequest.getKey());
            }

            String response = request.retrieve().bodyToMono(String.class).block();
            model.addAttribute("response", response);
        } catch (WebClientResponseException e) {
            model.addAttribute("response", "Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            model.addAttribute("response", "Error: " + e.getMessage());
        }

        model.addAttribute("apiRequest", apiRequest);
        return "api_form";
    }
}
