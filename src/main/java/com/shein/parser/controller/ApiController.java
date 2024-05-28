package com.shein.parser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.flattener.JsonFlattener;
import com.shein.parser.model.ApiRequest;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

            // Parse the API response
            JSONObject jsonResponse = new JSONObject(response);

            // Add the parsed data to the model
            model.addAttribute("jsonResponse", jsonResponse.toString(4)); // Optionally, you can format the JSON for better readability

            String flatJson = JsonFlattener.flatten(jsonResponse.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> flatJsonMap = objectMapper.readValue(flatJson, Map.class);

            // Split the keys and create a new map for each part
            List<List<String>> tableData = new ArrayList<>();
            for (Map.Entry<String, Object> entry : flatJsonMap.entrySet()) {
                List<String> row = new ArrayList<>(Arrays.asList(entry.getKey().split("\\.")));
                String value = entry.getValue() != null ? entry.getValue().toString() : ""; // Handle null value
                row.add(value);
                tableData.add(row);
            }
            // Add the parsed data to the model
            model.addAttribute("tableData", tableData);

        } catch (WebClientResponseException e) {
            model.addAttribute("response", "Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
        } catch (Exception e) {
            model.addAttribute("response", "Error: " + e.getMessage());
        }

        model.addAttribute("apiRequest", apiRequest);
        return "api_form";
    }

}
