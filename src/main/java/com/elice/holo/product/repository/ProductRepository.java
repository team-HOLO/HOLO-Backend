package com.elice.holo.product.repository;

import com.elice.holo.product.domain.Product;
import com.elice.holo.product.repository.query.ProductRepositoryCustom;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    //상품 상세 화면 쿼리
    @Query("select p from Product p join fetch p.productOptions po"
        + " where p.isDeleted = false and p.productId = :id")
    Optional<Product> findProductDetailByProductId(Long id);

    Optional<Product> findByProductIdAndIsDeletedFalse(Long id);

    @Query("select p from Product p join fetch p.productImages where p.isDeleted = false")
    List<Product> findAllWithProductImage();


}
