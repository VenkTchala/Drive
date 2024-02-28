package com.example.fileservice.service;

import com.example.fileservice.dto.FilePath;
import com.example.fileservice.dto.FileResponse;
import com.example.fileservice.dto.FileTreeStructure;
import com.example.fileservice.entity.DriveFile;
import com.example.fileservice.entity.FileMetaData;
import com.example.fileservice.entity.FileUser;
import com.example.fileservice.repository.FileMetaDataRepository;
import com.example.fileservice.repository.FileRepository;
import com.example.fileservice.repository.FileUserRepository;
import com.google.common.io.ByteSink;
import com.google.common.io.Files;
import com.scalified.tree.TreeNode;
import com.scalified.tree.multinode.ArrayMultiTreeNode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.desair.tus.server.TusFileUploadService;
import me.desair.tus.server.exception.TusException;
import me.desair.tus.server.upload.UploadInfo;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidParameterException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {
    private final FileMetaDataRepository fileMetaDataRepository;
    private final FileUserRepository fileUserRepository;
    private final FileRepository fileRepository;
    private final TusFileUploadService tusUploadService;
    private final int maxChunkSize = 50000;


    public void createUser(String username){
        if(fileUserRepository.existsByUsername(username))
            throw new InvalidParameterException(username);

        FileUser user = FileUser.builder()
                .username(username)
                .build();
        fileUserRepository.save(user);

        DriveFile userHomeFolder = DriveFile.builder()
                .fileName("Home")
                .parent(null)
                .path("/")
                .isDirectory(true)
                .build();

        fileRepository.save(userHomeFolder);

        Instant creationTime = Instant.now();

        FileMetaData homeFileMetaData = FileMetaData.builder()
                .size(0L)
                .owner(user)
                .file(userHomeFolder)
                .creationDate(creationTime)
                .modificationDate(creationTime)
                .build();

        fileMetaDataRepository.save(homeFileMetaData);
    }

    public void upload(HttpServletRequest request , HttpServletResponse response) throws IOException{
        this.tusUploadService.process(request, response);

        String uploadURI = request.getRequestURI();

        String uri = request.getRequestURI();

        UploadInfo uploadInfo = null;

        Path rootPath = Paths.get(ClassLoader.getSystemResource(".").getPath() + "/Storage");

        try {
            uploadInfo = this.tusUploadService.getUploadInfo(uploadURI);
        } catch (IOException | TusException e) {
            log.error("get upload info : ", e);
        }
        if (uploadInfo != null && !uploadInfo.isUploadInProgress()) {
            try (InputStream is = this.tusUploadService.getUploadedBytes(uploadURI)) {
                Path output = rootPath.resolve(uploadInfo.getFileName());
                Files.createParentDirs(output.toFile());
                log.error("hai");
//                java.nio.file.Files.copy(is, output, StandardCopyOption.REPLACE_EXISTING);

            final byte[] buffer = new byte[maxChunkSize];

            log.error("hai 2");

            int dataRead = is.read(buffer) ;

            log.error("size :" + dataRead);

            int i =0;

            List<File> filesChunks = new ArrayList<>();

            while (dataRead > -1) {
                File fileChunk = stageFile(buffer, dataRead);
                log.error("size :" + dataRead);
                log.error(" i : " + i++);
                filesChunks.add(fileChunk);
                dataRead = is.read(buffer);
//                Files.write(buffer,rootPath.toFile());
            }

            } catch (Exception e) {
                log.error("get uploaded bytes", e);
            }

            try {
                this.tusUploadService.deleteUpload(uploadURI);
            } catch (IOException | TusException e) {
                log.error("delete upload", e);
            }


            log.error("oooo");

        }
    }


    private File stageFile(byte[] buffer, int length) throws IOException {

        File storageFile = new File(ClassLoader.getSystemResource(".").getFile() + "/Storage");

        File outPutFile = File.createTempFile("file-", "-split", storageFile);

//        try(FileOutputStream fos = new FileOutputStream(outPutFile)) {
//            fos.write(buffer, 0, length);
//        }

        log.error("Filename : " + outPutFile.getPath());
        ByteSink byteSink = Files.asByteSink(outPutFile);
        byteSink.write(buffer);

        Files.touch(outPutFile);
        return outPutFile;
    }

    public void createFileStructure(String username , FileTreeStructure request){
        FileUser user = fileUserRepository.getFileUserByUsername(username)
                .orElseThrow(IllegalArgumentException::new);

        preOrderCreate(request,user);
    }


    private void preOrderCreate(FileTreeStructure node, FileUser user){

        String rootName = node.getFileName();

        DriveFile parent = getFileByPath(node.getParentPath());

    DriveFile nodeFile = DriveFile.builder()
            .fileName(rootName)
            .isDirectory(node.getIsDirectory())
            .path(node.getPath())
            .parent(parent)
            .children(new HashSet<>())
            .build();

        nodeFile = fileRepository.save(nodeFile);
        parent.getChildren().add(nodeFile);
        fileRepository.save(parent);
    //Save MetaData
        Instant now = Instant.now();

        FileMetaData nodeMetaData = FileMetaData.builder()
                .file(nodeFile)
                .size(node.getSize())
                .owner(user)
                .creationDate(now)
                .modificationDate(now)
                .build();

        fileMetaDataRepository.save(nodeMetaData);

    for(FileTreeStructure children : node.getChildren())
        preOrderCreate(children,user);

    }

    public void deleteFile(String username, FilePath path){
        FileUser user = fileUserRepository.getFileUserByUsername(username).orElseThrow(IllegalArgumentException::new);

        postOrderDelete(path.getPath(),user);
    }


    private void postOrderDelete(String path , FileUser user){

        DriveFile driveFile = fileRepository.findByPath(path).orElseThrow(IllegalArgumentException::new);

        for( DriveFile file  : driveFile.getChildren())
            postOrderDelete(file.getPath() , user);

        DriveFile file =
        fileRepository.findByPath(driveFile.getPath()).orElseThrow(IllegalArgumentException::new);

        FileMetaData metaData = fileMetaDataRepository.findByFile(file);

        fileMetaDataRepository.delete(metaData);

        fileRepository.delete(file);
    }



    public Set<FileResponse> getFilesInFolder(FilePath path , String username){
        DriveFile driveFile = fileRepository.findByPath(path.getPath())
                .orElseThrow(IllegalArgumentException::new);
        FileMetaData fileMetaData = fileMetaDataRepository.findByFile(driveFile);
        return driveFile.getChildren()
                .stream()
                .map(this::mapFileToResponse)
                .collect(Collectors.toSet());
    };

    public FileResponse mapFileToResponse(DriveFile file){
        FileMetaData metaData = file.getMetaData();
        String owner = metaData.getOwner().getUsername();

        return FileResponse.builder()
                .fileName(file.getFileName())
                .path(file.getPath())
                .size(metaData.getSize())
                .owner(owner)
                .creationDate(metaData.getCreationDate())
                .modificationDate(metaData.getModificationDate())
                .build();
    }

    public DriveFile getFileByPath(String path){
        return fileRepository.findByPath(path)
                .orElseThrow(IllegalArgumentException::new);
    }

    public FileTreeStructure testReadFiles(String username){

        log.error(username);
        File test = new File("/home/siva/Documents/assignments");
        Path base = Paths.get(test.getPath());

        Map<TreeNode<File>, FileTreeStructure> map = new HashMap<>();

        TreeNode<File> rootFile = new ArrayMultiTreeNode(test);


        for(File file : Files.fileTraverser().depthFirstPreOrder(test)){

            TreeNode<File> parent =
                    rootFile.find(file.getParentFile());

            if(parent != null){
                TreeNode<File> newFile = new ArrayMultiTreeNode<>(file);
                parent.add(newFile);
                String parentPath;
                String childPath;
                map.putIfAbsent(parent, FileTreeStructure
                        .builder()
                        .fileName(parent.data().getName())
                        .path("/")
                        .parent(new File(parent.data().getParent()).getName())
                        .parentPath(null)
                        .size(0L)
                        .children(new ArrayList<>())
                        .isDirectory(true)
                        .build()
                );

                childPath = base.relativize(newFile.data().toPath()).toString();
                parentPath = base.relativize(newFile.data().getParentFile().toPath()).toString();

                FileTreeStructure child = FileTreeStructure.builder()
                        .parent(new File(newFile.data().getParent()).getName())
                        .parentPath(parentPath)
                        .path(childPath)
                        .fileName(newFile.data().getName())
                        .build();

                if(newFile.data().isDirectory()){
                    child.setIsDirectory(true);
                    map.put(newFile,child);
                    child.setChildren(new ArrayList<>());
                    child.setSize(0L);
                }
                else {
                    child.setIsDirectory(false);
                    child.setChildren(null);
                    child.setSize(newFile.data().length());
                }

                map.get(parent).getChildren().add(child);
            }
        }

        return map.get(rootFile);
    }
}
