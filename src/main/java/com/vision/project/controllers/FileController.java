package com.vision.project.controllers;

import com.vision.project.models.File;
import com.vision.project.models.UserDetails;
import com.vision.project.services.base.FileService;
import com.vision.project.services.base.UserService;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping(value = "/images")
public class FileController {
    private final FileService fileService;
    private final UserService userService;

    public FileController(FileService fileService, UserService userService) {
        this.fileService = fileService;
        this.userService = userService;
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity<Resource> get(@PathVariable String fileName, HttpServletRequest request) throws MalformedURLException {
        Resource resource = fileService.getAsResource(fileName);
        String contentType;

        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException e) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                        resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/findByName/{resourceType}/{ownerId}")
    public File findByName(@PathVariable("resourceType") String resourceType, @PathVariable("ownerId") int ownerId){
        return fileService.findByName(resourceType, userService.getById(ownerId));
    }

    @DeleteMapping("/auth/delete/{resourceType}/{ownerId}")
    public boolean delete(@PathVariable("resourceType") String resourceType, @PathVariable("ownerId") int ownerId){
        UserDetails loggedUser = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication().getDetails();

        return fileService.delete(resourceType, userService.getById(ownerId), userService.findById(loggedUser.getId()));
    }
}
