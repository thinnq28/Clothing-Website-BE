package com.datn.shop_app.repository;

import com.datn.shop_app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByPhoneNumberAndActive(String phoneNumber, Boolean active);
    Optional<User> findByEmailAndActive(String email, Boolean active);
    Optional<User> findByEmail(String email);
    Optional<User> findByIdAndActive(Integer id, Boolean active);

    @Query("SELECT u FROM User u WHERE" +
            "(:name IS NULL OR :name = '' OR u.fullName LIKE %:name%) " +
            "AND (:phoneNumber IS NULL OR :phoneNumber = '' OR u.phoneNumber LIKE %:phoneNumber%)" +
            "AND (:email IS NULL OR :email = '' OR u.email LIKE %:email%)" +
            "AND (:active IS NULL OR u.active = :active)")
    Page<User> findAllUsers(@Param("name") String name, @Param("phoneNumber") String phoneNumber,
                            @Param("email") String email, @Param("active") Boolean active, Pageable pageable);

    @Query("SELECT DISTINCT u FROM User u " +
            "JOIN u.userRoles ur " +
            "JOIN ur.role r " +
            "WHERE r.roleName <> 'GUEST' " +
            "AND (:name IS NULL OR :name = '' OR u.fullName LIKE %:name%) " +
            "AND (:phoneNumber IS NULL OR :phoneNumber = '' OR u.phoneNumber LIKE %:phoneNumber%) " +
            "AND (:email IS NULL OR :email = '' OR u.email LIKE %:email%) " +
            "AND (:active IS NULL OR u.active = :active) " +
            "AND (:userId IS NULL OR u.id != :userId)")
    Page<User> findAllUsers(@Param("name") String name,
                                        @Param("phoneNumber") String phoneNumber,
                                        @Param("email") String email,
                                        @Param("active") Boolean active,
                                        @Param("userId") Integer userId,
                                        Pageable pageable);

    @Query("UPDATE User u set u.active = false WHERE u.id = :id")
    void updateActiveUser(@Param("id") Integer id);

    @Query("SELECT u FROM User u WHERE (u.email = :email OR u.phoneNumber = :phoneNumber)")
    Optional<User> findByEmailOrPhoneNumber(@Param("email") String email, @Param("phoneNumber") String phoneNumber);

    @Query("SELECT u FROM User u WHERE (u.phoneNumber = :phoneNumber and u.active = true)")
    Optional<User> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    List<User> findAllByIdIn(List<Integer> userIds);

    @Query("SELECT u FROM User u " +
            "WHERE EXISTS (" +
            "    SELECT 1 FROM Order o " +
            "    WHERE o.user = u AND o.total > :minTotal" +
            ")")
    List<User> findUsersWithOrderTotalGreaterThan(@Param("minTotal") BigDecimal minTotal);


}