package com.example.springboot_webapp.model;



import jakarta.persistence.*;



import org.springframework.stereotype.Component;



import java.util.Date;

@Entity
    @Component
    public class Product {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String name;
        private String description;
        private int price;
        private String brand;
        @Column(nullable = true)
        private Date releaseDate;
        @Column(nullable = true)
        private boolean productAvailable;
        @Column(nullable = true)
        private int stockQuantity;
        @Column(nullable = true)
        private String category;


    public Product() {
    }

    public Product(int id, String name, String description, int price, String brand, Date releaseDate, boolean productAvailable, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.brand = brand;
        this.releaseDate = releaseDate;
        this.productAvailable = productAvailable;
        this.stockQuantity = stockQuantity;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getBrand() {
        return brand;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date date) {
        this.releaseDate = date;
    }

    public boolean isProductAvailable() {
        return productAvailable;
    }

    public void setProductAvailable(boolean available) {
        this.productAvailable = available;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stock) {
        this.stockQuantity = stock;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
