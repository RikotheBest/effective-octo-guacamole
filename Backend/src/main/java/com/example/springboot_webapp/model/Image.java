package com.example.springboot_webapp.model;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

@Entity
@Component
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String imageName;
    private String imageType;
    private byte[] imageData;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;
    public Image() {
    }

    public Image(String imageName, String imageType, byte[] imageData) {
        this.imageName = imageName;
        this.imageType = imageType;
        this.imageData = imageData;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getId() {
        return product.getId();
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
