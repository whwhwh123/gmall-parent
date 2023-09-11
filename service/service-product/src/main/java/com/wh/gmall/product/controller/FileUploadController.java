package com.wh.gmall.product.controller;

import com.wh.gmall.common.result.Result;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@RestController
@RequestMapping("/admin/product")
public class FileUploadController {

    @Value("${minio.endpointUrl}")
    public String endpointUrl;

    @Value("${minio.accessKey}")
    public String accessKey;

    @Value("${minio.secreKey}")
    public String secreKey;

    @Value("${minio.bucketName}")
    public String bucketName;

    @PostMapping("/fileUpload")
    public Result<String> fileUpload(MultipartFile file) throws Exception{
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpointUrl)
                .credentials(accessKey, secreKey)
                .build();

        boolean isExist = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (isExist){
            System.out.println("bucket " + bucketName + " already exists");
        }
        else {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }

        String fileName = System.currentTimeMillis() + UUID.randomUUID().toString();
        minioClient.putObject(PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(fileName).stream(file.getInputStream(), file.getSize(), -1)
                                    .contentType(file.getContentType())
                                    .build());

        String url = endpointUrl + "/" + bucketName + "/" + fileName;
        System.out.println("url:" + url);
        return Result.ok(url);
    }
}
