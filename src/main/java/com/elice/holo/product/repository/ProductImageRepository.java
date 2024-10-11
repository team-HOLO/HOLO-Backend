package com.elice.holo.product.repository;

import com.elice.holo.product.domain.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    @Query("select pi from ProductImage pi where pi.product.productId = :id and pi.isDeleted = false")
    List<ProductImage> findByProductIdAndIsDeletedFalse(Long id);

    List<ProductImage> findAllByIsDeletedFalse();


}
