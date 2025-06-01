package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Image;
import com.datn.shop_app.entity.Product;
import com.datn.shop_app.entity.Variant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Integer> {
    Optional<Variant> findByIdAndActive(Integer id, Boolean active);

    @Query("SELECT v FROM Variant v WHERE" +
            "(:name IS NULL OR :name = '' OR v.variantName LIKE %:name%) " +
            "AND (:active IS NULL OR v.active = :active)")
    Page<Variant> findAllVariants(@Param("name") String name, @Param("active") Boolean active, Pageable pageable);

    @Query("SELECT v FROM Variant v WHERE" +
            "(:name IS NULL OR :name = '' OR v.variantName LIKE %:name%) " +
            "AND (:productName IS NULL OR :productName = '' OR v.product.productName LIKE %:productName%) " +
            "AND (:active IS NULL OR v.active = :active)")
    Page<Variant> findAllVariants(@Param("name") String name, @Param("productName") String productName,
                                  @Param("active") Boolean active, Pageable pageable);

    @Query("SELECT v FROM Variant v WHERE v.id IN :variantIds")
    List<Variant> getVariantsByIds(@Param("variantIds") List<Integer> variantIds);

    List<Variant> findAllByProductId(Integer productId);

    List<Variant> findAllByProductIdAndActive(Integer productId, Boolean active);

    @Query("SELECT v FROM Variant v WHERE v.id IN :ids AND v.active = true")
    List<Variant> findVariantByIds(@Param("ids") List<Integer> ids);

    Variant findBySkuIdAndActive(String skuId, Boolean active);

    @Query("SELECT v FROM Variant v WHERE LOWER(v.variantName) LIKE LOWER(CONCAT('%', :name, '%'))" +
            " AND v.active = true" +
            " AND v.quantity > 0")
    List<Variant> getVariantByName(@Param("name") String name);
}