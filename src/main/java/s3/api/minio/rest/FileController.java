package s3.api.minio.rest;

import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import s3.api.minio.service.FileService;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping(value = "/buckets/{bucketName}/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public void uploadFiles(@PathVariable String bucketName, @ParameterObject @Nullable String packageName,
                            @RequestBody @NotNull MultipartFile[] file) {
        fileService.uploadFiles(bucketName, packageName, file);
    }

    @GetMapping(path = "/{fileName}/url")
    @ResponseStatus(HttpStatus.OK)
    public String getPrimaryUrl(@NotNull @PathVariable String bucketName, @ParameterObject @Nullable String packageName,
                                @PathVariable String fileName, @ParameterObject @Nullable Integer seconds) {
        return fileService.getUrl(bucketName, packageName, fileName, seconds);
    }

    @DeleteMapping(path = "/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFile(@PathVariable String bucketName, @ParameterObject @Nullable String packageName,
                           @PathVariable String fileName) {
        fileService.deleteFile(bucketName, packageName, fileName);
    }

    @GetMapping("/names")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getFileNamesFromBucket(@PathVariable String bucketName) {
        return fileService.getFilesInfoFromBucket(bucketName).stream()
                .map(Item::objectName)
                .toList();
    }

    @PutMapping(path = "/{fileName}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public void updateFile(@PathVariable String bucketName, @ParameterObject @Nullable String packageName,
                           @PathVariable String fileName, @RequestBody @NotNull MultipartFile file) {
        fileService.updateFile(bucketName, packageName, fileName, file);
    }

    @GetMapping(value = "/{fileName}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Resource> downloadFile(@PathVariable String bucketName, @ParameterObject @Nullable String packageName,
                                                 @PathVariable String fileName) {
        try {
            InputStream inputStream = fileService.getObject(bucketName, packageName, fileName);

            ByteArrayResource resource = new ByteArrayResource(inputStream.readAllBytes());

            HttpHeaders headers = new HttpHeaders();
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"");

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
