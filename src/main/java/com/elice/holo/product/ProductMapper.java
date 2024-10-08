package com.elice.holo.product;

import com.elice.holo.product.domain.Product;
import com.elice.holo.product.domain.ProductOption;
import com.elice.holo.product.dto.AddProductRequest;
import com.elice.holo.product.dto.AddProductResponse;
import com.elice.holo.product.dto.ProductOptionDto;
import com.elice.holo.product.dto.ProductResponseDto;
import com.elice.holo.product.dto.ProductsResponseDto;
import com.elice.holo.product.dto.UpdateProductOptionDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ProductMapper {

//    Product toEntity(AddProductRequest request);
//
//    ProductOption optionToEntity(ProductOptionDto dto);
//
//    AddProductResponse toAddProductResponseDto(Product product);
//
//    AddProductRequest toAddProductRequestDto(Product product);
//
//    ProductResponseDto toProductDto(Product product);
//
//    ProductsResponseDto toProductsDto(Product product);
//
//    ProductOption optionDtoToEntity(UpdateProductOptionDto dto);
}
