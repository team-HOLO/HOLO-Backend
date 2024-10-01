package com.elice.holo.category.repository;

import com.elice.holo.category.domain.Category;
import com.elice.holo.category.dto.CategoryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByParentCategory(Category parent);}
