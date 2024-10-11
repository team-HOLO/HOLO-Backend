package com.elice.holo.category.repository;

import com.elice.holo.category.domain.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Category Id를 사용하여 카테고리 조회
    Optional<Category> findByCategoryIdAndIsDeletedFalse(Long id);

    // 전체 카테고리 목록 조회
    List<Category> findByIsDeletedFalse();

    // 상위 카테고리 객체를 이용한 하위 카테고리 조회
    List<Category> findByParentCategoryAndIsDeletedFalse(Category parent);

    // 대분류 카테고리 조회(상위 카테고리가 없는 카테고리 목록 조회)
    List<Category> findByIsDeletedFalseAndParentCategoryIsNull();

    // 같은 이름의 카테고리가 존재하는지 확인
    boolean existsByNameAndIsDeletedFalse(String name);

    // 검색 및 페이지네이션 적용해서 카테고리 조회
    Page<Category> findByIsDeletedFalseAndNameContainingIgnoreCase(String name, Pageable pageable);

    // 페이지네이션을 적용해서 카체고리 조회
    Page<Category> findByIsDeletedFalse(Pageable pageable);
}
