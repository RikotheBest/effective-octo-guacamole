package com.example.springboot_webapp.service;

import com.example.springboot_webapp.model.Image;
import com.example.springboot_webapp.model.Product;
import com.example.springboot_webapp.repo.ImageRepo;
import com.example.springboot_webapp.repo.Repo;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheEvict;

import org.springframework.data.domain.*;
import org.springframework.data.redis.connection.Limit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

import java.util.*;

@Service
public class ProductService {
    private Repo repo;
    private ImageRepo imageRepo;
    private RedisTemplate<String, Object> redisTemplate;
    private static final String PRODUCT_ZSET = "products";
    private static final String CATEGORY_ZSET = "products:category:";


    @Autowired
    public ProductService(ImageRepo imageRepo,Repo repo, RedisTemplate<String, Object> redisTemplate) {
        this.imageRepo = imageRepo;
        this.repo = repo;
        this.redisTemplate = redisTemplate;
    }

    public ProductService() {
    }

//    @Cacheable(value = "page", key = "#currentPage")
    public Page<Product> findAll(int currentPage, int pageSize){
        int start = currentPage * pageSize;
        int end = start + pageSize - 1;
        Long cachedProducts = redisTemplate.opsForZSet().size(PRODUCT_ZSET);
        long totalProducts = repo.count();
        if(cachedProducts == totalProducts){
            Set<Object> objects = redisTemplate.opsForZSet().range(PRODUCT_ZSET, start, end);
            List<Product> products = objects.stream()
                    .map(object -> (Product)object)
                    .toList();

            return new PageImpl<>(products, PageRequest.of(currentPage, pageSize), totalProducts);
        } else if (cachedProducts > totalProducts) {
            redisTemplate.getConnectionFactory().getConnection().execute("DEL " + PRODUCT_ZSET);
            Page<Product> page = repo.findAll(PageRequest.of(currentPage,pageSize));
            List<Product> products = repo.findAll();
            products.forEach(p -> redisTemplate.opsForZSet().add(PRODUCT_ZSET, p, p.getId()));
            return page;
        } else {
            Page<Product> page = repo.findAll(PageRequest.of(currentPage,pageSize));
            List<Product> products = repo.findAll();
            products.forEach(p -> redisTemplate.opsForZSet().add(PRODUCT_ZSET, p, p.getId()));
            return page;
        }
    }


    public Product findProduct(int id){
        Set<Object> set =  redisTemplate.opsForZSet().rangeByScore(PRODUCT_ZSET, id, id);
        if(!set.isEmpty()) return (Product) set.stream().findFirst().get();
        Product product = repo.findById(id).orElse(null);
        if(product == null) return product;
        redisTemplate.opsForZSet().add(PRODUCT_ZSET, product, product.getId());
        return product;
    }


    public void addProduct(Product product, MultipartFile multipartImage) throws IOException {
        Image image = new Image();
        image.setImageData(multipartImage.getBytes());
        image.setImageType(multipartImage.getContentType());
        image.setImageName(multipartImage.getOriginalFilename());
        image.setProduct(product);

        redisTemplate.opsForZSet().add(PRODUCT_ZSET, product, product.getId());
        imageRepo.save(image);
        repo.save(product);
    }


    public byte[] getImageById(int id) {
        String encodedImage = (String)redisTemplate.opsForValue().get("image::" + id);
        byte[] decodedImage;
        if(encodedImage != null){
            decodedImage = Base64.getDecoder().decode(encodedImage);
        }else {
            decodedImage = imageRepo.findByProductId(id).getImageData();
            redisTemplate.opsForValue().set("image::" + id, decodedImage);
        }

        return decodedImage;

    }

    public void updateProduct(int id, Product product, MultipartFile multipartImage) throws IOException {
        Image image = new Image();
        image.setImageName(multipartImage.getOriginalFilename());
        image.setImageData(multipartImage.getBytes());
        image.setImageType(multipartImage.getContentType());
        image.setProduct(product);
        redisTemplate.opsForZSet().remove(PRODUCT_ZSET, product.getId(), product.getId());
        redisTemplate.opsForZSet().add(PRODUCT_ZSET, product, product.getId());
        redisTemplate.opsForValue().set("image::"+id, multipartImage.getBytes());
        repo.save(product);
        imageRepo.save(image);
    }

    @CacheEvict(value = "image", key = "#id")
    public void deleteProduct(int id) {
        repo.deleteById(id);
        imageRepo.deleteByProductId(id);
        redisTemplate.opsForZSet().remove(PRODUCT_ZSET, id);
    }

    public Page<Product> filter(String category, int currentPage, int pageSize) {
        int offset = currentPage * pageSize;
        Long cachedProducts = redisTemplate.opsForZSet().size(CATEGORY_ZSET + category);
        long totalProducts = repo.countByCategory(category);
        System.out.println(totalProducts);

        if(cachedProducts == totalProducts){
//            System.out.println("if called");
             List<Product> products = redisTemplate.opsForZSet()
                     .range(CATEGORY_ZSET + category, offset, pageSize)
                     .stream()
                     .map(object -> (Product) object)
                     .toList();
             return new PageImpl<>(products, PageRequest.of(currentPage, pageSize), totalProducts);
        } else if (cachedProducts > totalProducts) {
//            System.out.println("else if called");
            redisTemplate.getConnectionFactory().getConnection().execute("DEL " + CATEGORY_ZSET + category);
            Page<Product> page = repo.findAllByCategory(category, PageRequest.of(currentPage, pageSize));
            List<Product> filteredProducts = repo.findAllByCategory(category);
            filteredProducts.forEach(p -> redisTemplate.opsForZSet().add(CATEGORY_ZSET + category, p, 1));
            return page;
        } else{
//            System.out.println("else called");
            Page<Product> page = repo.findAllByCategory(category, PageRequest.of(currentPage, pageSize));
            List<Product> filteredProducts = repo.findAllByCategory(category);
            filteredProducts.forEach(p -> redisTemplate.opsForZSet().add(CATEGORY_ZSET + category, p, 1));
            return page;
        }
    }


    public List<Product> searchFor(String keyword) {
//        long start = System.currentTimeMillis();
//        Set<Object> objects = redisTemplate.opsForZSet().range(PRODUCT_ZSET, 0, -1);
//        List<Product> products = null;
//        if(objects.isEmpty()){
//            products = repo.findAll();
//        } else {
//            products = objects.stream()
//                    .map(object -> (Product) object)
//                    .toList();
//        }
//
//        List<Product> filteredProducts = products.stream()
//                .filter((product -> product.getDescription().toLowerCase().contains(keyword.toLowerCase())
//                        || product.getBrand().toLowerCase().contains(keyword.toLowerCase())
//                        || product.getName().toLowerCase().contains(keyword.toLowerCase())))
//                .toList();
//        System.out.println(System.currentTimeMillis() - start);
//        return filteredProducts;

        List<Product> products = repo.findAllByKeyword(keyword);

        return products;


    }


}
