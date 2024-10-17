package com.elice.holo.category.mapper;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryCreateDto;
import com.elice.holo.category.dto.CategoryDetailsDto;
import com.elice.holo.category.dto.CategoryDto;
import com.elice.holo.category.dto.CategoryListDto;
import com.elice.holo.category.dto.CategoryResponseDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-10-18T03:09:58+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 21.0.4 (Amazon.com Inc.)"
)
@Component
public class CategoryMapperImpl implements CategoryMapper {

    @Override
    public CategoryDto toCategoryDto(Category category) {
        if ( category == null ) {
            return null;
        }

        Long categoryId = null;
        String name = null;

        categoryId = category.getCategoryId();
        name = category.getName();

        CategoryDto categoryDto = new CategoryDto( categoryId, name );

        return categoryDto;
    }

    @Override
    public CategoryResponseDto toCategoryResponseDto(Category category) {
        if ( category == null ) {
            return null;
        }

        List<CategoryDto> subCategories = null;
        Long categoryId = null;
        String name = null;

        subCategories = toCategoryDtoList( category.getSubCategories() );
        categoryId = category.getCategoryId();
        name = category.getName();

        CategoryResponseDto categoryResponseDto = new CategoryResponseDto( categoryId, name, subCategories );

        return categoryResponseDto;
    }

    @Override
    public Category toEntity(CategoryCreateDto categoryCreateDto) {
        if ( categoryCreateDto == null ) {
            return null;
        }

        Category.CategoryBuilder category = Category.builder();

        category.name( categoryCreateDto.getName() );
        category.description( categoryCreateDto.getDescription() );

        return category.build();
    }

    @Override
    public CategoryDetailsDto toCategoryDetailsDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryDto parentCategory = null;
        Long categoryId = null;
        String name = null;
        String description = null;

        parentCategory = toCategoryDto( category.getParentCategory() );
        categoryId = category.getCategoryId();
        name = category.getName();
        description = category.getDescription();

        CategoryDetailsDto categoryDetailsDto = new CategoryDetailsDto( categoryId, name, description, parentCategory );

        return categoryDetailsDto;
    }

    @Override
    public List<CategoryDto> toCategoryDtoList(List<Category> categories) {
        if ( categories == null ) {
            return null;
        }

        List<CategoryDto> list = new ArrayList<CategoryDto>( categories.size() );
        for ( Category category : categories ) {
            list.add( toCategoryDto( category ) );
        }

        return list;
    }

    @Override
    public CategoryListDto toCategoryListDto(Category category) {
        if ( category == null ) {
            return null;
        }

        CategoryListDto.CategoryListDtoBuilder categoryListDto = CategoryListDto.builder();

        categoryListDto.categoryId( category.getCategoryId() );
        categoryListDto.parentId( categoryParentCategoryCategoryId( category ) );
        categoryListDto.name( category.getName() );
        categoryListDto.description( category.getDescription() );

        return categoryListDto.build();
    }

    private Long categoryParentCategoryCategoryId(Category category) {
        if ( category == null ) {
            return null;
        }
        Category parentCategory = category.getParentCategory();
        if ( parentCategory == null ) {
            return null;
        }
        Long categoryId = parentCategory.getCategoryId();
        if ( categoryId == null ) {
            return null;
        }
        return categoryId;
    }
}
