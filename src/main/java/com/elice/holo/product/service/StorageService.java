package com.elice.holo.product.service;

import com.elice.holo.product.domain.ProductImage;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    ProductImage uploadImage(MultipartFile multipartFile) throws IOException;

    List<ProductImage> uploadImages(List<MultipartFile> multipartFiles) throws IOException;

//    void deleteFile(String fileName);
}
