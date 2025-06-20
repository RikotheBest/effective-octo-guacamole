package com.example.springboot_webapp.repo;

import com.example.springboot_webapp.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;



import java.util.List;



@Repository
public interface Repo extends JpaRepository<Product, Integer> {

    @Query("SELECT p from Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findAllByKeyword(String keyword);

    @Query("SELECT p from Product p WHERE LOWER(p.category) = LOWER(:category)")
    Page<Product> findAllByCategory(String category, Pageable pageable);

    @Query("SELECT p from Product p WHERE LOWER(p.category) = LOWER(:category)")
    List<Product> findAllByCategory(String category);


//    @Query("SELECT count(p) from Product p WHERE LOWER(p.category) = LOWER(:category)")
//    long countByCategory(String category);
}
