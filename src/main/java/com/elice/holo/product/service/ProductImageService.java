package com.elice.holo.product.service;

import com.elice.holo.product.domain.Product;
import com.elice.holo.product.domain.ProductImage;
import com.elice.holo.product.dto.ProductImageDto;
import com.elice.holo.product.repository.ProductImageRepository;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final StorageService storageService;

    //상품 이미지 추가
    @Transactional
    public void saveItemImages(List<MultipartFile> multipartFiles, Product product, List<Boolean> isThumbnails)
        throws IOException {
        if (multipartFiles.size() != isThumbnails.size()) {
            throw new IllegalArgumentException("이미지 개수와 썸네일 체크 리스트 불일치.");
        }

        List<ProductImage> productImages = storageService.uploadImages(multipartFiles);

        for (int i = 0; i < productImages.size(); i++) {
            ProductImage productImage = productImages.get(i);
            if(isThumbnails.get(i)){
                productImage.changeIsThumbnail(true);
            }
            productImage.assignProduct(product);
        }

        productImageRepository.saveAll(productImages);
    }

    //상품 상세 화면 -> 상품 이미지 조회
    public List<ProductImageDto> findProductImageDetail(Long productId) {
        List<ProductImage> productImages = productImageRepository.findByProductImageIdAndIsDeletedFalse(
            productId);

        return productImages.stream()
            .map(ProductImageDto::new)
            .collect(Collectors.toList());
    }









}
