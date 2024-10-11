package com.elice.holo.category.mapper;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryDto;
import com.elice.holo.category.dto.CategoryListDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    // Category 엔티티를 CategoryDto로 변환
    CategoryDto toCategoryDto(Category category);

    // Category 엔티티를 CategoryResponseDto로 변환
    @Mapping(source = "subCategories", target = "subCategories")
    CategoryResponseDto toCategoryResponseDto(Category category);

    // CategoryCreateDto를 Category 엔티티로 변환 (생성할 때 사용)
    @Mapping(target = "parentCategory", ignore = true)
    // parentCategory는 서비스에서 설정할 예정이므로 무시
    Category toEntity(CategoryCreateDto categoryCreateDto);

    // Category Entity를 CategoryDetailsDto로 변환
    @Mapping(source = "parentCategory", target = "parentCategory")
    CategoryDetailsDto toCategoryDetailsDto(Category category);

    // List<Category>를 List<CategoryDto>로 변환
    List<CategoryDto> toCategoryDtoList(List<Category> categories);

    // Category Entity를 CategoryListDto로 변환
    @Mapping(target = "categoryId", source = "categoryId")
    // parentCategory가 null일 경우 자동으로 null 처리됨
    @Mapping(target = "parentId", source = "parentCategory.categoryId")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    CategoryListDto toCategoryListDto(Category category);
}