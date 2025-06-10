package com.example.springboot_webapp.service;

import com.example.springboot_webapp.model.Product;
import com.example.springboot_webapp.repo.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;

@Service
public class ProductService {
    private Repo repo;
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    public ProductService(Repo repo, RedisTemplate<String, Object> redisTemplate) {
        this.repo = repo;
        this.redisTemplate = redisTemplate;
    }

    public ProductService() {
    }

//    @Cacheable(value = "products")
    public Page<Product> findAll(int currentPage, int pageSize){
        return  repo.findAll(PageRequest.of(currentPage, pageSize));
    }

    @Cacheable(value = "product", key = "#id")
    public Product findProduct(int id){
        return repo.findById(id).orElse(null);
    }

    @CachePut(value = "product", key = "#product.id")
    public Product addProduct(Product product, MultipartFile image) throws IOException {
        product.setImageData(image.getBytes());
        product.setImageType(image.getContentType());
        product.setImageName(image.getOriginalFilename());
        return repo.save(product);
    }


    public byte[] getImageById(int id) {
        String encodedImage = (String) redisTemplate.opsForValue().get("image::" + id);
        byte[] decodedImage;
        if(encodedImage != null){
            decodedImage = Base64.getDecoder().decode(encodedImage);
        }else {
            decodedImage = this.findProduct(id).getImageData();
            redisTemplate.opsForValue().set("image::" + id, decodedImage, Duration.ofDays(1));
        }

        return decodedImage;

    }

    public void updateProduct(int id, Product product, MultipartFile image) throws IOException {
        product.setImageName(image.getOriginalFilename());
        product.setImageData(image.getBytes());
        product.setImageType(image.getContentType());
        redisTemplate.opsForValue().setIfPresent("product::"+id, product, Duration.ofDays(1));
        redisTemplate.opsForValue().setIfPresent("image::"+id, image.getBytes(), Duration.ofDays(1));
        repo.save(product);
    }

    @Caching(evict = {@CacheEvict(value = "product", key = "#id"), @CacheEvict(value = "image", key = "#id")})
    public void deleteProduct(int id) {
        repo.deleteById(id);
    }


    public List<Product> searchFor(String keyword) {
        List<Product> products = (List<Product>) redisTemplate.opsForValue().get("products::SimpleKey []");
        if(products.isEmpty()){
            products = repo.findAll();
            redisTemplate.opsForValue().set("products::SimpleKey[]", products, Duration.ofDays(1));
        }
        return products.stream()
                .filter((product -> product.getDescription().toLowerCase().contains(keyword.toLowerCase())
                        || product.getBrand().toLowerCase().contains(keyword.toLowerCase())
                        || product.getName().toLowerCase().contains(keyword.toLowerCase())))
                .toList();

    }
}
