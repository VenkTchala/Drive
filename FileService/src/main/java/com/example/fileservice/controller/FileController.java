package com.example.fileservice.controller;

import com.example.fileservice.dto.DeleteRequest;
import com.example.fileservice.dto.FileRequest;
import com.example.fileservice.dto.Status;
import com.example.fileservice.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    @PostMapping("/createuser")
    public void createUser(@RequestHeader("loggedInUser") String username) throws IOException{
         fileService.createUser(username);
    }
    @PostMapping("/deletefile")
    public Status deleteFiles(@RequestHeader("loggedInUser") String username, @RequestBody DeleteRequest request){
    return fileService.deleteFile(request.getId() , username);
    }

    @PostMapping("/restorefile")
    public Status restoreFile(@RequestHeader("loggedInUser") String username, @RequestBody DeleteRequest request){
        return fileService.restoreFile(request, username);
    }

    @GetMapping("/files")
    public List<FileRequest> getAllFiles(@RequestHeader("loggedInUser") String username){
        return fileService.getFilesOfUser(username);
    }

    @GetMapping("/trash")
    public List<FileRequest> getAllFilesInTrash(@RequestHeader("loggedInUser")String username){
        return fileService.getUserTrash(username);
    }

    @GetMapping(path = "/download")
    public ResponseEntity<Resource> download(@RequestHeader("loggedInUser") String username , @RequestParam String name) {
        return fileService.downloadFile(username,name);
    }

}
