package com.example.springboot_webapp.service;

import com.example.springboot_webapp.model.Product;
import com.example.springboot_webapp.repo.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Service
public class ProductService {
    private Repo repo;


    @Autowired
    public ProductService(Repo repo) {
        this.repo = repo;
    }

    public ProductService() {
    }

    @Cacheable(value = "products")
    public List<Product> findAll(){
        System.out.println("fetching the products from database...");
        List<Product> products = repo.findAll();
        return products;
    }

    @Cacheable(value = "product", key = "#id")
    public Product findProduct(int id){
        return repo.findById(id).orElse(null);
    }

    @CachePut(value = "product", key = "#product.id")
    public void addProduct(Product product, MultipartFile image) throws IOException {
        product.setImageData(image.getBytes());
        product.setImageType(image.getContentType());
        product.setImageName(image.getOriginalFilename());
        repo.save(product);
    }

    @Cacheable(value = "image", key = "#id")
    public byte[] getImageById(int id) {
        Product product = findProduct(id);
        return product.getImageData();
    }

    @CachePut(value = "product", key = "#id")
    public void updateProduct(int id, Product product, MultipartFile image) throws IOException {
        product.setImageName(image.getOriginalFilename());
        product.setImageData(image.getBytes());
        product.setImageType(image.getContentType());
        repo.updateProductById(product.getName(), product.getDescription(), product.getPrice(), product.getBrand(), product.getReleaseDate(),
                product.isProductAvailable(), product.getStockQuantity(), product.getImageName(), product.getImageType(), product.getImageData(), product.getCategory(),id);
    }

    @CacheEvict(value = "product", key = "#id")
    public void deleteProduct(int id) {
        repo.deleteById(id);
    }

//    @Cacheable(value = "products", condition = "")
    public List<Product> searchFor(String keyword) {
        return repo.findAllByKeyword(keyword);
    }
}
