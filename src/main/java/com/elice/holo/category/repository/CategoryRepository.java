package com.elice.holo.category.repository;

import com.elice.holo.category.domain.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryIdAndIsDeletedFalse(Long id);

    List<Category> findByIsDeletedFalse();

    List<Category> findByParentCategoryAndIsDeletedFalse(Category parent);
}
