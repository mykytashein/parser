package com.shein.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Map;

@Controller
public class FileUploadController {

    @RequestMapping(value = "/uploadFile", method = {RequestMethod.GET, RequestMethod.POST})
    public String handleFileUpload(@RequestParam(value = "file", required = false) MultipartFile file, RedirectAttributes redirectAttributes, Model model) {

        // If the request is a POST request and a file has been uploaded
        if (file != null && !file.isEmpty()) {
            try {
                // Parse the file
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> jsonMap = objectMapper.readValue(file.getInputStream(), Map.class);

                // Add the parsed data to the model
                model.addAttribute("jsonContent", jsonMap);

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
