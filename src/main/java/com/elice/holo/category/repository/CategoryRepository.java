package com.elice.holo.category.repository;

import com.elice.holo.category.domain.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Category Id를 사용하여 카테고리 조회
     *
     * @param id 카테고리 ID
     * @return 해당 categoryId의 Category 객체
     */
    Optional<Category> findByCategoryIdAndIsDeletedFalse(Long id);

    /**
     * 전체 카테고리 목록 조회
     *
     * @return 삭제되지 않은 카테고리 객체 목록
     */
    List<Category> findByIsDeletedFalse();

    /**
     * 상위 카테고리 객체를 이용한 하위 카테고리 조회
     *
     * @param parent 상위 카테고리 객체
     * @return 해당 카테고리의 하위 카테고리 객체 목록
     */
    List<Category> findByParentCategoryAndIsDeletedFalse(Category parent);

    /**
     * 대분류 카테고리 조회(상위 카테고리가 없는 카테고리 목록 조회)
     *
     * @return 대분류 카테고리 목록
     */
    List<Category> findByIsDeletedFalseAndParentCategoryIsNull();

}
