package com.example.fileservice.controller;

import com.example.fileservice.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
@CrossOrigin(exposedHeaders = { "Location", "Upload-Offset" })
@RequiredArgsConstructor
@RequestMapping("/file")
@Slf4j
public class Uploadcontroller {

    private final FileService fileService;
//    private final Path uploadPath = Paths.get(ClassLoader.getSystemResource("./storage").getPath());
    @RequestMapping(value = { "/upload", "/upload/**" }, method = { RequestMethod.POST,
            RequestMethod.PATCH, RequestMethod.HEAD, RequestMethod.DELETE, RequestMethod.GET })
    public void upload(HttpServletRequest servletRequest,
                       HttpServletResponse servletResponse ) throws IOException {
        fileService.upload(servletRequest,servletResponse);
    }

}
