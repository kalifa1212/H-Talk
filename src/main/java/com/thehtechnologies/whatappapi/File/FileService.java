package com.thehtechnologies.whatappapi.File;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static java.io.File.separator;
import static java.lang.System.currentTimeMillis;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileService {

    @Value("${application.file.uploads.media-output-path}")
    private String fileUploadPath;

    public String saveFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull String userId
    ) {
        final String fileUploadSubPath = "users" + separator + userId;
        return uploadFile(sourceFile, fileUploadSubPath);
    }
    //TODO user fileuploadpaht to dir
    public  void MirasaveFile( String FileName, MultipartFile multipartFile) throws IOException{
        Path uploadDir = Paths.get(fileUploadPath+"/profile/image");
        if(!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
            log.info("folder create");
        }
        try (InputStream inputStream=multipartFile.getInputStream()){
            Path filePath = uploadDir.resolve(FileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("file save");
        }catch (IOException ioe) {
            throw new IOException("Impossible d'enregistrer l'image:"+FileName,ioe);
        }
    }
    private String uploadFile(
            @Nonnull MultipartFile sourceFile,
            @Nonnull String fileUploadSubPath
    ) {
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);

        if (!targetFolder.exists()) {
            boolean folderCreated = targetFolder.mkdirs();
            if (!folderCreated) {
                log.warn("Failed to create the target folder: " + targetFolder);
                return null;
            }
        }
        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalUploadPath + separator + currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try {
            Files.write(targetPath, sourceFile.getBytes());
            log.info("File saved to: " + targetFilePath);
            return targetFilePath;
        } catch (IOException e) {
            log.error("File was not saved", e);
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1).toLowerCase();
    }
}
