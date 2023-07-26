package s3.api.minio.rest;

import io.minio.messages.Bucket;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import s3.api.minio.service.StorageService;

import java.util.List;

@RestController
@RequestMapping(value = "/buckets")
@RequiredArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @GetMapping("/names")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getBucketsName() {
        List<Bucket> buckets = storageService.getBuckets();
        return buckets.stream()
                .map(Bucket::name)
                .toList();
    }

    @PostMapping("/{bucketName}")
    @ResponseStatus(HttpStatus.CREATED)
    public void createBucket(@PathVariable String bucketName) {
        storageService.tryCreateBucket(bucketName);
    }
}
