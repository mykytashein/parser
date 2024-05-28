package com.shein.parser.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.json.XML;
import org.json.JSONObject;
import com.github.wnameless.json.flattener.JsonFlattener;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Controller
public class FileUploadController {

    @RequestMapping(value = "/uploadFile", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleFileUpload(@RequestParam(value = "file", required = false) MultipartFile file,
                                   RedirectAttributes redirectAttributes, Model model) {

        if (file != null && !file.isEmpty()) {
            try {
                String contentType = file.getContentType();
                String content = new String(file.getBytes(), StandardCharsets.UTF_8);
                JSONObject json;
                assert contentType != null;
                if (contentType.contains("/xml")) {
                    json = XML.toJSONObject(content);
                } else {
                    json = new JSONObject(content);
                }

                // Flatten the JSON
                String flatJson = JsonFlattener.flatten(json.toString());
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> flatJsonMap = objectMapper.readValue(flatJson, Map.class);

                List<List<String>> tableData = new ArrayList<>();
                for (Map.Entry<String, Object> entry : flatJsonMap.entrySet()) {
                    List<String> row = new ArrayList<>(Arrays.asList(entry.getKey().split("\\.")));
                    String value = entry.getValue() != null ? entry.getValue().toString() : "";
                    row.add(value);
                    tableData.add(row);
                }

                model.addAttribute("tableData", tableData);

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
