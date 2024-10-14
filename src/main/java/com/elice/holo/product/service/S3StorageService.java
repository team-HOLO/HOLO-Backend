package com.elice.holo.product.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.elice.holo.product.domain.ProductImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Primary
@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService{

    private final AmazonS3Client s3Client;

    @Value("${cloud.aws.s3.bucket.name}")
    private String bucketName;

    @Override
    public ProductImage uploadImage(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()) {
            throw new IllegalArgumentException("한장 이상의 상품 사진을 등록해주세요.");
        }

        String originName = multipartFile.getOriginalFilename();
        String storeImageName = LocalStorageService.getStoreImageName(originName);
        ObjectMetadata metaData = createObjectMetadata(multipartFile);

        s3Client.putObject(bucketName, storeImageName, multipartFile.getInputStream(), metaData);

        return ProductImage.createProductImage(originName, storeImageName);
    }

    @Override
    public List<ProductImage> uploadImages(List<MultipartFile> multipartFiles) throws IOException {
        List<ProductImage> productImages = new ArrayList<>();

        for (MultipartFile multipartfile : multipartFiles) {
            productImages.add(uploadImage(multipartfile));
        }

        return productImages;
    }

    @Override
    public String getProductImageUrl(String storeName) {
        return s3Client.getUrl(bucketName, storeName).toString();
    }


    private ObjectMetadata createObjectMetadata(MultipartFile file) {
        ObjectMetadata metaData = new ObjectMetadata();
        metaData.setContentLength(file.getSize());
        metaData.setContentType(file.getContentType());
        return metaData;
    }


}
