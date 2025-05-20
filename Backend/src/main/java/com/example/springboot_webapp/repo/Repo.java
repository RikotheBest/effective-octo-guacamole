package com.example.springboot_webapp.repo;

import com.example.springboot_webapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;



@Repository
public interface Repo extends JpaRepository<Product, Integer> {
    @Transactional
    @Modifying
    @NativeQuery(value = "UPDATE product SET name = ?, description= ?, price = ?, brand = ?, release_date = ?, product_available = ?, stock_quantity = ?, image_name = ?, image_type = ?, image_data = ?, category = ? WHERE id = ?")
    void updateProductById(String name, String description, int price, String brand, Date releaseDate, boolean productAvailable,
                           int stockQuantity, String imageName, String imageType, byte[] imageData, String category, int id);

//    @Transactional
//    @Modifying
//    @NativeQuery(value = "DELETE FROM product WHERE id = ?")
//    void deleteProductById(int id);



    @Query("SELECT p from Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> findAllByKeyword(String keyword);
}
