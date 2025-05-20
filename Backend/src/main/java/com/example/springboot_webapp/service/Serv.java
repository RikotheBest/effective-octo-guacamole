package com.example.springboot_webapp.service;

import com.example.springboot_webapp.model.Product;
import com.example.springboot_webapp.repo.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@Service
public class Serv {
    private Repo repo;

    @Autowired
    public Serv(Repo repo) {
        this.repo = repo;
    }

    public Serv() {
    }
    public List<Product> findAll(){
        return repo.findAll();
    }
    public Product findProduct(int id){
        return repo.findById(id).orElse(null);
    }

    public void addProduct(Product product, MultipartFile image) throws IOException {
        product.setImageData(image.getBytes());
        product.setImageType(image.getContentType());
        product.setImageName(image.getOriginalFilename());
        repo.save(product);
    }

    public byte[] getImageById(int id) {
        Product product = findProduct(id);
        return product.getImageData();
    }

    public void updateProduct(int id, Product product, MultipartFile image) throws IOException {
        product.setImageName(image.getOriginalFilename());
        product.setImageData(image.getBytes());
        product.setImageType(image.getContentType());
        repo.updateProductById(product.getName(), product.getDescription(), product.getPrice(), product.getBrand(), product.getReleaseDate(),
                product.isProductAvailable(), product.getStockQuantity(), product.getImageName(), product.getImageType(), product.getImageData(), product.getCategory(),id);
    }

    public void deleteProduct(int id) {
        repo.deleteById(id);
    }

    public List<Product> searchFor(String keyword) {
        return repo.findAllByKeyword(keyword);
    }
}
