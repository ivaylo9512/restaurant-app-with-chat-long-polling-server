package unit;

import com.vision.project.exceptions.FileFormatException;
import com.vision.project.models.File;
import com.vision.project.models.UserModel;
import com.vision.project.repositories.base.FileRepository;
import com.vision.project.services.FileServiceImpl;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.EntityNotFoundException;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {
    @Mock
    private FileRepository fileRepository;

    private FileServiceImpl fileService;

    private final static String uploadsPath = "./uploads/test";

    @BeforeAll
    private static void setup() throws IOException {
        new java.io.File(uploadsPath + "/logo.txt").createNewFile();
        new java.io.File(uploadsPath + "/logo1.txt").createNewFile();
        new java.io.File(uploadsPath + "/logo3.txt").createNewFile();
    }

    @AfterAll
    private static void reset() throws IOException {
        new java.io.File(uploadsPath + "/logo.txt").delete();
        new java.io.File(uploadsPath + "/logo1.txt").delete();
        new java.io.File(uploadsPath + "/logo2.txt").delete();
        new java.io.File(uploadsPath + "/logo3.txt").delete();
    }

    @BeforeEach
    private void setupEach() throws IOException {
        this.fileService = Mockito.spy(new FileServiceImpl(fileRepository, uploadsPath));
    }

    @Test
    public void generate() {
        MockMultipartFile file = new MockMultipartFile(
                "image132",
                "image132.png",
                "image/png",
                "image132".getBytes());

        File savedFile = fileService.generate(file, "logo", "image");

        assertEquals(savedFile.getResourceType(), "logo");
        assertEquals(savedFile.getType(), "image/png");
    }

    @Test
    public void generate_WhenTypeDoesNotMatch_FileFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "text132",
                "text132.txt",
                "text/plain",
                "text132".getBytes());

        FileFormatException thrown = assertThrows(FileFormatException.class,
                () -> fileService.generate(file, "logo", "image"));

        assertEquals(thrown.getMessage(), "File should be of type image");
    }

    @Test
    public void generate_WhenTypeIsNull_FileFormat() {
        MockMultipartFile file = new MockMultipartFile(
                "text132",
                "text132.txt",
                null,
                "text132".getBytes());

        FileFormatException thrown = assertThrows(FileFormatException.class,
                () -> fileService.generate(file, "logo", "image"));

        assertEquals(thrown.getMessage(), "File should be of type image");
    }

    @Test
    public void createAndSave() throws Exception{
        FileInputStream input = new FileInputStream("./uploads/test/logo.txt");
        MultipartFile multipartFile = new MockMultipartFile("test", "logo.txt", "text/plain",
                IOUtils.toByteArray(input));
        input.close();

        fileService.save("logo2", multipartFile);

        assertTrue(new java.io.File(uploadsPath +  "/logo2.txt").exists());
    }

    @Test
    public void delete_WithOwner(){
        UserModel owner = new UserModel();
        owner.setId(1);

        File file = new File();
        file.setOwner(owner);
        file.setExtension("txt");

        when(fileRepository.findByType("logo", owner)).thenReturn(Optional.of(file));

        boolean isDeleted = fileService.delete("logo", owner, owner);

        assertFalse(new java.io.File(uploadsPath + "/logo1.txt").exists());
        assertTrue(isDeleted);
    }

    @Test
    public void delete_WithAdmin(){
        UserModel loggedUser = new UserModel();
        loggedUser.setRole("ROLE_ADMIN");
        loggedUser.setId(1);

        UserModel owner = new UserModel();
        owner.setId(3);

        File file = new File();
        file.setOwner(owner);
        file.setExtension("txt");

        when(fileRepository.findByType("logo", owner)).thenReturn(Optional.of(file));

        boolean isDeleted = fileService.delete("logo", owner, loggedUser);

        assertFalse(new java.io.File(uploadsPath + "/logo3.txt").exists());
        assertTrue(isDeleted);
    }

    @Test
    public void delete_WhenFileIsNotInDB_NotFound(){
        UserModel loggedUser = new UserModel();
        loggedUser.setRole("ROLE_ADMIN");

        UserModel owner = new UserModel();
        owner.setId(11);

        when(fileRepository.findByType("logo", owner)).thenReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> fileService.delete("logo", owner, loggedUser));

        assertEquals(thrown.getMessage(), "File not found.");
    }

    @Test
    public void delete_WhenFileIsNotInFolder(){
        UserModel loggedUser = new UserModel();
        loggedUser.setId(2);
        loggedUser.setRole("ROLE_ADMIN");

        UserModel owner = new UserModel();
        owner.setId(11);

        File file = new File();
        file.setOwner(owner);

        when(fileRepository.findByType("logo", owner)).thenReturn(Optional.of(file));

        boolean isDeleted = fileService.delete("logo", owner, loggedUser);
        assertFalse(isDeleted);

        verify(fileRepository, times(0)).delete(any(File.class));
    }

    @Test
    public void getAsResource() throws MalformedURLException {
        Resource resource = fileService.getAsResource("logo.txt");

        assertEquals(resource.getFilename(), "logo.txt");
    }

    @Test
    public void getAsResource_WhenFileNonexistent(){
        EntityNotFoundException thrown = assertThrows(EntityNotFoundException.class,
                () -> fileService.getAsResource("nonexistent.txt"));

        assertEquals(thrown.getMessage(), "File not found");
    }
}
