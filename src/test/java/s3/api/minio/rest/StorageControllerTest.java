package s3.api.minio.rest;

import io.minio.messages.Bucket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import s3.api.minio.service.StorageService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("StorageController test should")
@WebMvcTest(controllers = StorageController.class)
class StorageControllerTest {

    public static final String TEST_FIRST_BUCKET_NAME = "test-bucket-1";
    public static final String TEST_SECOND_BUCKET_NAME = "test-bucket-2";

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private StorageService storageService;

    @Test
    @DisplayName("get buckets name")
    void testGetBucketsName() throws Exception {
        List<Bucket> buckets = getBuckets();
        when(storageService.getBuckets()).thenReturn(buckets);

        mockMvc.perform(get("/buckets")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]").value(TEST_FIRST_BUCKET_NAME))
                .andExpect(jsonPath("$[1]").value(TEST_SECOND_BUCKET_NAME));
    }

    private static List<Bucket> getBuckets() throws Exception {
        Bucket firstBucket = createBucket(TEST_FIRST_BUCKET_NAME);
        Bucket secondBucket = createBucket(TEST_SECOND_BUCKET_NAME);

        return Arrays.asList(firstBucket, secondBucket);
    }

    private static Bucket createBucket(String bucketName) throws Exception {
        var bucket = new Bucket();
        Field name = bucket.getClass().getDeclaredField("name");
        name.setAccessible(true);
        name.set(bucket, bucketName);
        return bucket;
    }
}