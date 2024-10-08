package com.elice.holo.product.service;

import com.elice.holo.product.ProductMapper;
import com.elice.holo.product.domain.ProductOption;
import com.elice.holo.product.dto.AddProductRequest;
import com.elice.holo.product.dto.ProductOptionDto;
import com.elice.holo.product.domain.Product;
import com.elice.holo.product.dto.UpdateProductOptionDto;
import com.elice.holo.product.dto.UpdateProductRequest;
import com.elice.holo.product.exception.ProductNotFoundException;
import com.elice.holo.product.repository.ProductRepository;
import com.elice.holo.product.dto.ProductsResponseDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    //상품 추가를 위한 메서드
    @Transactional
    public Product saveProduct(AddProductRequest request) {

        Product newProduct = request.toEntity();
//        Product newProduct = productMapper.toEntity(request);

        //옵션 리스트 받아와서 Product 에 추가
        request.getProductOptions().stream()
            .map(ProductOptionDto::toEntity)
            .collect(Collectors.toList()).forEach(newProduct::addProductOption);

//        request.getProductOptions().stream()
//            .map(productMapper::optionToEntity)
//            .collect(Collectors.toList()).forEach(newProduct::addProductOption);

        return productRepository.save(newProduct);
    }

    //상품 단일 조회(상세 조회)를 위한 메서드
    public Product findProductById(Long id) {
        return productRepository.findProductDetailByProductId(id)
            .orElseThrow(() -> new ProductNotFoundException("상품이 존재하지 않습니다."));
    }

    //상품 다수 조회(목록 조회)를 위한 메서드
    public List<ProductsResponseDto> findProducts() {
        return productRepository.findAll().stream()
            .map(ProductsResponseDto::new)
            .collect(Collectors.toList());

//        return productRepository.findAll().stream()
//            .map(productMapper::toProductsDto)
//            .collect(Collectors.toList());
    }

    //상품 수정 메서드
    @Transactional
    public Long updateProduct(Long productId, UpdateProductRequest request) {

        Product product = findProductById(productId);

        product.updateProduct(request.getName(), request.getPrice(),
            request.getDescription(), request.getStockQuantity()
        );

        addProductOptions(request, product);

        return product.getProductId();
    }

    //상품 삭제 메서드(soft delete)
    @Transactional
    public void deleteProduct(Long productId) {
        Product product = productRepository.findByProductIdAndIsDeletedFalse(productId)
            .orElseThrow(() -> new ProductNotFoundException("삭제할 상품이 존재하지 않습니다."));

        product.updateIsDeleted(true);
    }

    //상품 옵션 수정시 추가 메서드
    private void addProductOptions(UpdateProductRequest request, Product product) {

        //기존 상품 옵션과 수정 상품 옵션 비교해서 기존 상품 옵션 삭제
        List<ProductOption> existProductOptions = new ArrayList<>(product.getProductOptions());
        List<UpdateProductOptionDto> newProductOptions = request.getProductOptions();

        for (ProductOption existOption : existProductOptions) {
            boolean isExist = newProductOptions.stream().anyMatch(newOption ->
                newOption.getId() != null && newOption.getId().equals(existOption.getProductOptionId())
            );

            //상품 옵션 삭제
            if (!isExist) existOption.updateIsDeleted(true);
        }

        newProductOptions.forEach(newOption -> {
            if (newOption.getId() == null) {
                product.addProductOption(newOption.toEntity());
//                product.addProductOption(productMapper.optionDtoToEntity(newOption));
            } else {
                existProductOptions.stream()
                    .filter(exist -> exist.getProductOptionId().equals(newOption.getId()))
                    .findFirst()
                    .ifPresent(exist -> {
                        exist.updateProductOption(newOption.getColor(), newOption.getSize(),
                            newOption.getOptionQuantity());
                    });
            }
        });
    }
}
