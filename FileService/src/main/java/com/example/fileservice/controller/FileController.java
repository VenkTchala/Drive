package com.example.fileservice.controller;

import com.example.fileservice.dto.FilePath;
import com.example.fileservice.dto.FileResponse;
import com.example.fileservice.dto.FileTreeStructure;
import com.example.fileservice.entity.FileUser;
import com.example.fileservice.repository.FileUserRepository;
import com.example.fileservice.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController()
@RequestMapping("/file")
@Slf4j
@RequiredArgsConstructor
public class FileController {
    private final FileService fileService;
    private final FileUserRepository fileUserRepository;

    @GetMapping
    public FileTreeStructure getFiles(@RequestHeader("loggedInUser") String username){
        return  fileService.testReadFiles(username);
    }

    @PostMapping("/createuser")
    public void createUser(@RequestHeader("loggedInUser") String username){
         fileService.createUser(username);
    }

    public void copyFiles(@RequestHeader("loggedInUser") String username , FileTreeStructure request){
        fileService.createFileStructure(username, request);
    }

    public Set<FileResponse> getFilesInFolder(@RequestHeader("loggedInUser") String username , FilePath path){
        return fileService.getFilesInFolder(path,username);
    }
    @PostMapping("/deletefile")
    public void deleteFiles(@RequestHeader("loggedInUser") String username, FilePath filePath){
        fileService.deleteFile(username,filePath);
    }

}
