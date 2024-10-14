package com.elice.holo.product.service;

import com.elice.holo.product.domain.ProductImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalStorageService implements StorageService{

    @Value("${file.dir}")
    private String fileDir;

    //파일 경로명
    public String getFullPath(String filename) {
        return fileDir + filename;
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
    public ProductImage uploadImage(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()) { 
            throw new IllegalArgumentException("한장 이상의 상품 사진을 등록해주세요.");
        }
        
        String originName = multipartFile.getOriginalFilename();
        String storeImageName = getStoreImageName(originName);

        multipartFile.transferTo(new File(getFullPath(storeImageName)));

        return ProductImage.createProductImage(originName, storeImageName);
    }

    @Override
    public String getProductImageUrl(String storeName) {
        return getFullPath(storeName);
    }


//    @Override
//    public void deleteFile(String fileName) {
//
//    }

    protected static String getStoreImageName(String originName) {
        String ext = extractExt(originName);  //jpeg
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    //확장자 추출
    protected static String extractExt(String oriImageName) {
        int pos = oriImageName.lastIndexOf(".");
        return oriImageName.substring(pos + 1);
    }


}
