package com.example.springboot_webapp.repo;

import com.example.springboot_webapp.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface ImageRepo extends JpaRepository<Image, Integer> {
    @Query("select image from Image image where image.product.id = ?1")
    Image findByProductId(int id);


    @Query("delete from Image image where image.product.id = ?1")
    void deleteByProductId(int id);

}
