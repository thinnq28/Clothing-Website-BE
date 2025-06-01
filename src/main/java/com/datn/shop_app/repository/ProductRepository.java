package com.datn.shop_app.repository;

import com.datn.shop_app.entity.Product;
import com.datn.shop_app.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByIdAndActive(Integer id, Boolean active);

    @Query("SELECT p FROM Product p WHERE" +
            "(:name IS NULL OR :name = '' OR p.productName LIKE %:name%) " +
            "AND (:supplier_name IS NULL OR :supplier_name = '' OR p.supplier.supplierName LIKE %:supplier_name%) " +
            "AND (:commodity_name IS NULL OR :commodity_name = '' OR p.commodity.commodityName LIKE %:commodity_name%) " +
            "AND (:active IS NULL OR p.active = :active)")
    Page<Product> findAllProducts(@Param("name") String name, @Param("supplier_name") String supplierName, @Param("commodity_name") String commodityName,
    @Param("active") Boolean active, Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE (:name IS NULL OR :name = '' OR p.productName LIKE %:name%) " +
            "AND (:supplier_name IS NULL OR :supplier_name = '' OR p.supplier.supplierName LIKE %:supplier_name%) " +
            "AND (:commodity_name IS NULL OR :commodity_name = '' OR p.commodity.commodityName LIKE %:commodity_name%) " +
            "AND p.variants IS NOT EMPTY " +
            "AND (:active IS NULL OR p.active = :active) " +
            "AND (:maxPrice IS NULL OR :maxPrice = 0 OR EXISTS (" +
            "    SELECT v FROM Variant v " +
            "    WHERE v MEMBER OF p.variants " +
            "    AND v.price <= :maxPrice " +
            "    AND v.id = (SELECT MIN(v2.id) FROM Variant v2 WHERE v2 MEMBER OF p.variants)" +
            ")) " +
            "AND (:rating IS NULL OR :rating = 0 OR " +
            "     (SELECT AVG(c.rating) FROM CommentRate c WHERE c.product.id = p.id AND c.active = true) >= :rating)")
    Page<Product> findAllProductClient(@Param("name") String name,
                                       @Param("supplier_name") String supplierName,
                                       @Param("commodity_name") String commodityName,
                                       @Param("active") Boolean active,
                                       @Param("maxPrice") Double maxPrice,
                                       @Param("rating") Integer rating,
                                       Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR :name = '' OR p.productName LIKE %:name%) " +
            "AND (:active IS NULL OR p.active = :active)")
    List<Product> findProductsByName(@Param("name") String name, @Param("active") Boolean active);

    @Query("SELECT p FROM Product p WHERE " +
            " (:active IS NULL OR p.active = :active)")
    List<Product> findProductsAndActive(@Param("active") Boolean active);

    @Query("SELECT MAX(v.price) FROM Product p JOIN p.variants v WHERE p.active = true")
    Double findMaxPrice();

    List<Product> findAllByIdIn(List<Integer> ids);
}