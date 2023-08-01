package upbrella.be.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import upbrella.be.store.entity.StoreDetail;
import upbrella.be.store.entity.StoreImage;
import upbrella.be.store.repository.StoreDetailRepository;
import upbrella.be.store.repository.StoreImageRepository;

import java.io.IOException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreImageService {

    private final S3Client s3Client;
    private final StoreImageRepository storeImageRepository;
    private final StoreDetailRepository storeDetailRepository;

    @Value("${AWS_S3_BUCKET}")
    private String bucketName;

    @Transactional
    public String uploadFile(MultipartFile file, long storeDetailId, String randomId) {

        String fileName = file.getOriginalFilename() + randomId;
        String contentType = file.getContentType();

        // Upload file
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key("store-image/" + fileName)
                .acl("public-read")
                .contentDisposition("inline")
                .contentType(contentType)
                .build();

        String url = "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/store-image/" + fileName;

        try {
            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            saveStoreImage(url, storeDetailId);
            return url;
        } catch (IOException e) {
            throw new IllegalStateException("Failed to upload file", e);
        }
    }

    @Transactional
    public void deleteFile(long imageId) {

        StoreImage storeImage = storeImageRepository.findById(imageId)
                .orElseThrow(() -> new IllegalStateException("해당 이미지가 존재하지 않습니다."));
        storeImageRepository.deleteById(imageId);
        deleteFileInS3(storeImage.getImageUrl());
    }

    public String makeRandomId() {

        return UUID.randomUUID().toString().substring(0, 10);
    }

    private void deleteFileInS3(String imgUrl) {

        String key = parseKey(imgUrl);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    private void saveStoreImage(String imageUrl, long storeDetailId) {

        StoreDetail storeDetail = storeDetailRepository.getReferenceById(storeDetailId);
        storeImageRepository.save(StoreImage.createStoreImage(storeDetail, imageUrl));
    }


    private String parseKey(String url) {

        String[] splitUrl = url.split("/");

        return splitUrl[splitUrl.length - 2] + "/" + splitUrl[splitUrl.length - 1];
    }
}