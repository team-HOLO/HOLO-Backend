package com.elice.holo.product.repository;

import com.elice.holo.product.domain.ProductImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductImageIdAndIsDeletedFalse(Long id);

    List<ProductImage> findAllByIsDeletedFalse();


}
