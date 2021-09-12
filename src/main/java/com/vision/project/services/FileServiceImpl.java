package com.vision.project.services;

import com.vision.project.exceptions.FileFormatException;
import com.vision.project.exceptions.FileStorageException;
import com.vision.project.exceptions.UnauthorizedException;
import com.vision.project.models.File;
import com.vision.project.models.UserModel;
import com.vision.project.repositories.base.FileRepository;
import com.vision.project.services.base.FileService;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService {
    private final Path fileLocation;
    private final FileRepository fileRepository;

    public FileServiceImpl(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.fileLocation = Paths.get("./uploads")
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileLocation);
        } catch (Exception e) {
            throw new RuntimeException("Couldn't create directory");
        }
    }

    @Override
    public boolean delete(String resourceType, UserModel owner, UserModel loggedUser) {
        if(owner.getId() != loggedUser.getId()
                && !loggedUser.getRole().equals("ROLE_ADMIN")){
            throw new UnauthorizedException("Unauthorized");
        }

        File file = findByName(resourceType, owner);
        if(file == null){
            throw new EntityNotFoundException("File not found.");
        }

        boolean isDeleted = new java.io.File("./uploads/" + resourceType + owner.getId() + "." + file.getExtension()).delete();
        if(isDeleted){
            fileRepository.delete(file);
            return true;
        }

        return false;
    }

    @Override
    public Resource getAsResource(String fileName){
        try {
            Path filePath = this.fileLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                throw new EntityNotFoundException("File not found");
            }

            return resource;
        } catch (MalformedURLException e) {
            throw new FileFormatException(e.getMessage());
        }
    }

    @Override
    public File findByName(String resourceType, UserModel owner){
        return fileRepository.findByName(resourceType, owner);
    }

    @Override
    public void save(String name, MultipartFile receivedFile) {
        try {
            String extension = FilenameUtils.getExtension(receivedFile.getOriginalFilename());
            String fileName = name + "." + extension;

            Path targetLocation = this.fileLocation.resolve(fileName);
            Files.copy(receivedFile.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new FileStorageException("Couldn't store the image.");
        }
    }

    @Override
    public File generate(MultipartFile receivedFile, String resourceType, String fileType) {
        String extension = FilenameUtils.getExtension(receivedFile.getOriginalFilename());
        String contentType = receivedFile.getContentType();

        if (contentType == null || !contentType.startsWith(fileType)) {
            throw new FileFormatException("File should be of type " + fileType);
        }

        return new File(resourceType, receivedFile.getSize(), receivedFile.getContentType(), extension);
    }
}
