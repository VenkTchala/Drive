package com.example.fileservice.service;

import com.example.fileservice.dto.DeleteRequest;
import com.example.fileservice.dto.FileRequest;
import com.example.fileservice.dto.RenameRequest;
import com.example.fileservice.dto.Status;
import com.example.fileservice.entity.DeletedFile;
import com.example.fileservice.entity.DriveFile;
import com.example.fileservice.entity.FileMetadata;
import com.example.fileservice.entity.FileUser;
import com.example.fileservice.repository.DeletedFileRepository;
import com.example.fileservice.repository.FileRepository;
import com.example.fileservice.repository.FileUserRepository;
import com.google.common.io.Files;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    private final FileUserRepository fileUserRepository;
    private final FileRepository fileRepository;
    private final TusFileUploadService tusUploadService;
    private final DeletedFileRepository deletedFileRepository;

    public void createUser(String username) throws IOException {
        if (fileUserRepository.existsByUsername(username))
            throw new InvalidParameterException(username);

        FileUser user = FileUser.builder()
                .username(username)
                .build();
        fileUserRepository.save(user);

        Path rootPath = Paths.get("Storage", username);
        Files.createParentDirs(rootPath.toFile());
    }

    public void upload(HttpServletRequest request, HttpServletResponse response) throws IOException {
        this.tusUploadService.process(request, response);

        String email = request.getHeader("loggedInUser");

        String fileIdString = request.getHeader("fileId");

        Long filedId = null;

        if (fileIdString != null)
            filedId = Long.parseLong(fileIdString);


        String uploadURI = request.getRequestURI();

        UploadInfo uploadInfo = null;

        Path rootPath = Paths.get("Storage", email);
        try {
            uploadInfo = this.tusUploadService.getUploadInfo(uploadURI);
        } catch (IOException | TusException e) {
            log.error("get upload info : ", e);
        }
        if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
            try (InputStream is = this.tusUploadService.getUploadedBytes(uploadURI)) {
                Path output = rootPath.resolve(uploadInfo.getFileName());
                Files.createParentDirs(output.toFile());
                java.nio.file.Files.copy(is, output, StandardCopyOption.REPLACE_EXISTING);

                FileUser user = fileUserRepository.findDistinctByUsername(email).orElseThrow(IllegalArgumentException::new);

                DriveFile file;

                if (fileIdString == null) {
                    file = DriveFile.builder()
                            .fileName(uploadInfo.getFileName())
                            .metaData(
                                    FileMetadata.builder()
                                            .size(uploadInfo.getLength())
                                            .creationDate(Instant.now())
                                            .build()
                            )
                            .user(user)
                            .build();
                } else {

                    file = fileRepository.findById(filedId)
                            .orElseThrow(IllegalArgumentException::new);

                    String oldName = file.getFileName();

                    file.setFileName(uploadInfo.getFileName());
                    FileMetadata metadata = file.getMetaData();
                    metadata.setSize(uploadInfo.getLength());
                    metadata.setCreationDate(Instant.now());

                    forgetFile(oldName, email);
                }

                fileRepository.save(file);

            } catch (Exception e) {
                log.error("get uploaded bytes", e);
            }

            try {
                this.tusUploadService.deleteUpload(uploadURI);
            } catch (IOException | TusException e) {
                log.error("delete upload", e);
            }
        }
    }


    public Status deleteFile(Long id, String email) {
        DriveFile file;
        try {
            file = fileRepository.findById(id)
                    .orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException exception) {
            return Status.builder()
                    .status(false)
                    .errMsg("Something went wrong")
                    .build();
        }

        log.error("Owner : " + file.getUser().getUsername() + " email : " + email);

        if (!file.getUser().getUsername().equals(email))
            return Status.builder()
                    .status(false)
                    .errMsg("Acess denied for file")
                    .build();

        FileUser user;

        try {

            user = fileUserRepository.findDistinctByUsername(email)
                    .orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException exception) {
            return Status.builder()
                    .status(false)
                    .errMsg("Something went wrong")
                    .build();
        }

        DeletedFile deletedFile =
                DeletedFile.builder()
                        .user(user)
                        .deletionInstant(LocalDate.now())
                        .metaData(FileMetadata.builder()
                                .creationDate(file.getMetaData().getCreationDate())
                                .size(file.getMetaData().getSize())
                                .build())
                        .name(file.getFileName())
                        .build();

        deletedFileRepository.save(deletedFile);

        fileRepository.delete(file);

        return Status.builder()
                .status(true)
                .errMsg("file deleted sucessfully")
                .build();
    }

    public List<FileRequest> getUserTrash(String username) {
        FileUser user;

        try {
            user = fileUserRepository.findDistinctByUsername(username).orElseThrow(IllegalArgumentException::new);

            return deletedFileRepository.getDeletedFilesByUser(user)
                    .stream().map(i -> FileRequest.builder()
                            .id(i.getId())
                            .name(i.getName())
                            .size(i.getMetaData().getSize())
                            .modificationDate(formatDate(i.getMetaData().getCreationDate()))
                            .owner(user.getUsername())
                            .build())
                    .toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    public Status restoreFile(DeleteRequest request, String email) {

        DeletedFile file;
        try {
            file = deletedFileRepository.findById(request.getId())
                    .orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException exception) {
            return Status.builder()
                    .status(false)
                    .errMsg("Something went wrong")
                    .build();
        }

        if (!file.getUser().getUsername().equals(email))
            return Status.builder()
                    .status(false)
                    .errMsg("Acess denied for file")
                    .build();

        FileUser user;

        try {

            user = fileUserRepository.findDistinctByUsername(email)
                    .orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException exception) {
            return Status.builder()
                    .status(false)
                    .errMsg("Something went wrong")
                    .build();
        }

        DriveFile restoredFile =
                DriveFile.builder()
                        .user(user)
                        .metaData(FileMetadata.builder()
                                .creationDate(file.getMetaData().getCreationDate())
                                .size(file.getMetaData().getSize())
                                .build())
                        .fileName(file.getName())
                        .build();

        fileRepository.save(restoredFile);

        deletedFileRepository.delete(file);

        return Status.builder()
                .status(true)
                .errMsg("file restored sucessfully")
                .build();
    }

    public void forgetFile(String name, String email) {
        Path rootPath = Paths.get("Storage", email);

        rootPath
                .resolve(name)
                .toFile().delete();
    }

    public ResponseEntity<Resource> downloadFile(String username, String fileName) {
        Path rootPath = Paths.get("Storage", username,fileName);

        ByteArrayResource resource;
        try {
            resource = new ByteArrayResource(java.nio.file.Files.readAllBytes(rootPath));


            return ResponseEntity.ok()
                    .contentLength(rootPath.toFile().length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (IOException ioException) {
            return ResponseEntity.
                    notFound()
                    .build();
        }

    }

    public Status renameFile(String email, RenameRequest request) {

        DriveFile file;

        try {
            file = fileRepository.findById(request.getId())
                    .orElseThrow(IllegalArgumentException::new);
        } catch (IllegalArgumentException exception) {
            return Status.builder()
                    .status(false)
                    .errMsg("Something went wrong")
                    .build();
        }

        if (!file.getUser().getUsername().equals(email))
            return Status.builder()
                    .status(false)
                    .errMsg("Access denied for file")
                    .build();

        FileUser user;
        user = fileUserRepository.findDistinctByUsername(email)
                .orElseThrow(IllegalArgumentException::new);

        if(fileRepository.existsByUserAndFileName(user, request.getNewName())){
            return Status.builder()
                    .status(false)
                    .errMsg("file with name " + request.getNewName() + " already exists.")
                    .build();
        }

        Path source = Paths.get("Storage", email, file.getFileName());
        file.setFileName(request.getNewName());

        try {

            java.nio.file.Files.move(source, source.resolveSibling(request.getNewName()));
        }
        catch (IOException exception){

            log.error(exception.getClass().toString());
            log.error(exception.getMessage());

            return Status.builder()
                    .status(false)
                    .errMsg("Something went wrong")
                    .build();
        }

        fileRepository.save(file);
        return Status.builder()
                .status(true)
                .errMsg(null)
                .build();

    }

    public List<FileRequest> getFilesOfUser(String email) {
        FileUser user;
        List<DriveFile> files;
        try {
            user = fileUserRepository.findDistinctByUsername(email)
                    .orElseThrow(IllegalArgumentException::new);
            files = fileRepository.getDriveFilesByUser(user);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
        return files.stream().map(i -> FileRequest.builder()
                        .id(i.getId())
                        .name(i.getFileName())
                        .size(i.getMetaData().getSize())
                        .modificationDate(formatDate(i.getMetaData().getCreationDate()))
                        .owner(user.getUsername())
                        .build())
                .toList();
    }

    private String formatDate(Instant instant) {
        return DateTimeFormatter.ofPattern("dd-MM-yy")
                .withZone(ZoneId.of("Asia/Kolkata"))
                .format(instant);
    }
}
