package s3.api.minio.rest;

import io.minio.messages.Contents;
import io.minio.messages.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import s3.api.minio.service.FileService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("FileControllerTest should")
@WebMvcTest(controllers = FileController.class)
class FileControllerTest {

    public static final String REQUEST_MAPPING = "/buckets/{bucketName}/files/";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private FileService fileService;

    @Test
    @DisplayName("delete file")
    void testDeleteFile() throws Exception {
        String bucketName = "test-bucket";
        String packageName = "testPackage";
        String fileName = "testFile.jpg";

        mockMvc.perform(delete(REQUEST_MAPPING + "/{fileName}", bucketName, fileName)
                        .param("packageName", packageName))
                .andExpect(status().isOk());

        verify(fileService)
                .deleteFile(bucketName, packageName, fileName);
    }

    @Test
    @DisplayName("get file from bucket")
    void testGetFileNamesFromBucket() throws Exception {
        String bucketName = "test";

        String firstFileName = "Рисунок.png";
        String secondFileName = "Рисунок1.png";
        List<Item> items = List.of(
                new Contents(firstFileName),
                new Contents(secondFileName)
        );
        when(fileService.getFilesInfoFromBucket(bucketName)).thenReturn(items);

        mockMvc.perform(get(REQUEST_MAPPING + "/names", bucketName))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value(firstFileName))
                .andExpect(jsonPath("$[1]").value(secondFileName));
    }
}