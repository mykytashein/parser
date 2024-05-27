package com.shein.parser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//import javax.xml.parsers.DocumentBuilderFactory;
import org.json.XML;
import org.json.JSONObject;
import com.github.wnameless.json.flattener.JsonFlattener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class FileUploadController {

    @RequestMapping(value = "/uploadFile", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleFileUpload(@RequestParam(value = "file", required = false) MultipartFile file, RedirectAttributes redirectAttributes, Model model) {

        // If the request is a POST request and a file has been uploaded
        if (file != null && !file.isEmpty()) {
            try {
                // Parse the file
                String contentType = file.getContentType();
                String content = new String(file.getBytes(), StandardCharsets.UTF_8);
                JSONObject json;
                assert contentType != null;
                if (contentType.contains("/xml")) {
                    json = XML.toJSONObject(content); // Convert XML to JSON
                } else {
                    json = new JSONObject(content); // Parse JSON
                }

                // Flatten the JSON
                String flatJson = JsonFlattener.flatten(json.toString());
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> flatJsonMap = objectMapper.readValue(flatJson, Map.class);

                // Split the keys and create a new map for each part
                List<List<String>> tableData = new ArrayList<>();
                for (Map.Entry<String, Object> entry : flatJsonMap.entrySet()) {
                    List<String> row = new ArrayList<>(Arrays.asList(entry.getKey().split("\\.")));
                    row.add(entry.getValue().toString());
                    tableData.add(row);
                }

                // Add the parsed data to the model
                model.addAttribute("tableData", tableData);

                // Redirect with success message
                redirectAttributes.addFlashAttribute("message",
                        "You successfully uploaded '" + file.getOriginalFilename() + "'");

            } catch (IOException e) {
                e.printStackTrace();
                redirectAttributes.addFlashAttribute("message",
                        "Could not upload file '" + file.getOriginalFilename() + "' => " + e.getMessage());
            }
        }

        return "uploadFile";
    }
}