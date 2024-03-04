package com.example;

import com.example.util.CommandListner;
import io.tus.java.client.*;
import org.apache.commons.net.ProtocolCommandEvent;
import org.apache.commons.net.ProtocolCommandListener;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import javax.crypto.Cipher;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws Exception
    {
        run();
//        try {
////            FTPClient ftpClient = ftpClient();
////            long currentTimeMillis = System.currentTimeMillis();
//
////            String directoryPath = String.format("/create-directory-test-%d", currentTimeMillis);
////            String path = String.format("/test-upload-file-%d.jpg", currentTimeMillis);
////            String newPath = String.format("/test-rename-file-%d.jpg", currentTimeMillis);
////
////            ftpClient.makeDirectory("/one");
////            ftpClient.removeDirectory("drive");
////            printTree("/", ftpClient);
////            ftpClient.enterRemotePassiveMode();
////            uploadFile("/home/siva/code/docker/ftpserver/Dockerfile",  "/drive/Dockerfile", ftpClient);
////            printTree("/", ftpClient);
//            encrypt();
//        }
//        catch (Exception e){
//            System.out.println(e.getMessage());
//            System.out.println(e.getClass());
//            System.out.println("failed");
//        }
    }

    private static FTPClient ftpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.addProtocolCommandListener(new CommandListner());

        ftpClient.connect("localhost", 3221);
        ftpClient.login("admin", "password");
        ftpClient.enterRemotePassiveMode();
        ftpClient.setControlEncoding("UTF-8");
        ftpClient.setAutodetectUTF8(true);
        return ftpClient;
    }

    private static void encrypt() throws Exception{
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair pair = keyPairGenerator.generateKeyPair();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();

        String secretMessage = "Baeldung secret message";

        Cipher enCipher = Cipher.getInstance("RSA");
        enCipher.init(Cipher.ENCRYPT_MODE,publicKey);

        byte[] secretMessageBytes = secretMessage.getBytes(StandardCharsets.UTF_8);
        byte[] encryptedMessageBytes = enCipher.doFinal(secretMessageBytes);

        String encodedMessage = Base64.getEncoder().encodeToString(encryptedMessageBytes);

        System.out.println(encodedMessage);

        Cipher decryptCipher = Cipher.getInstance("RSA");
        decryptCipher.init(Cipher.DECRYPT_MODE,privateKey);

        byte[] decryptedMessageBytes = decryptCipher.doFinal(encryptedMessageBytes);
        String decryptedMessage = new String(decryptedMessageBytes,StandardCharsets.UTF_8);

        System.out.println(secretMessage.equals(decryptedMessage));

    }


    private static void printTree(String path, FTPClient ftpClient) throws Exception {
        for (FTPFile ftpFile : ftpClient.listFiles(path)) {
            System.out.println();
            System.out.printf("[printTree][%d]\n", System.currentTimeMillis());
            System.out.printf("[printTree][%d] Get name : %s \n", System.currentTimeMillis(), ftpFile.getName());
            System.out.printf("[printTree][%d] Get timestamp : %s \n", System.currentTimeMillis(), ftpFile.getTimestamp().getTimeInMillis());
            System.out.printf("[printTree][%d] Get group : %s \n", System.currentTimeMillis(), ftpFile.getGroup());
            System.out.printf("[printTree][%d] Get link : %s \n", System.currentTimeMillis(), ftpFile.getLink());
            System.out.printf("[printTree][%d] Get user : %s \n", System.currentTimeMillis(), ftpFile.getUser());
            System.out.printf("[printTree][%d] Get type : %s \n", System.currentTimeMillis(), ftpFile.getType());
            System.out.printf("[printTree][%d] Is file : %s \n", System.currentTimeMillis(), ftpFile.isFile());
            System.out.printf("[printTree][%d] Is directory : %s \n", System.currentTimeMillis(), ftpFile.isDirectory());
            System.out.printf("[printTree][%d] Formatted string : %s \n", System.currentTimeMillis(), ftpFile.toFormattedString());
            System.out.println();

            if (ftpFile.isDirectory()) {
                printTree(path + File.separator + ftpFile.getName(), ftpClient);
            }
        }
    }

    public static void uploadFile(String localPath, String remotePath, FTPClient ftpClient) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(localPath);
        System.out.println();
        System.out.printf("[uploadFile][%d] Is success to upload file : %s -> %b",
                System.currentTimeMillis(), remotePath, ftpClient.storeFile(remotePath, fileInputStream));
        System.out.println();
    }



    private static void run() throws Exception{
        var httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

        Path testFile = Paths.get("/home/siva/Videos/Apollo.gif");

        var client = new TusClient();

        client.setHeaders(Map.of("Authorization", "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJzaXZhQHNrby5jb20iLCJpYXQiOjE3MDk1MjA1MzEsImV4cCI6MTcwOTUyMjMzMX0.8bA_ZzFLsc6q2K5JfGgcxDoFhKgrC-W7vbc4czQd93s",
                "filename" , testFile.getFileName().toString(),
                "parentpath", "/"
                ));

        client.setUploadCreationURL(URI.create("http://localhost:8080/file/upload").toURL());
        client.enableResuming(new TusURLMemoryStore());

        TusUpload upload = new TusUpload(testFile.toFile());
        var executor = new TusExecutor() {

            @Override
            protected void makeAttempt() throws ProtocolException, IOException {
                TusUploader uploader = client.resumeOrCreateUpload(upload);
                uploader.setChunkSize(1024);

                do {
                    long totalBytes = upload.getSize();
                    long bytesUploaded = uploader.getOffset();
                    double progress = (double) bytesUploaded / totalBytes * 100;

                    System.out.printf("Upload at %6.2f %%.%n", progress);
                }
                while (uploader.uploadChunk() > -1);

                uploader.finish();
            }

        };

        executor.setDelays(new int[]{2, 4, 8});

        boolean success = executor.makeAttempts();
    }
}
