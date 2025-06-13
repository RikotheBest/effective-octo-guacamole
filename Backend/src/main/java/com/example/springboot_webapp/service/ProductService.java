package com.example.springboot_webapp.service;

import com.example.springboot_webapp.model.Image;
import com.example.springboot_webapp.model.Product;
import com.example.springboot_webapp.repo.ImageRepo;
import com.example.springboot_webapp.repo.Repo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Set;

@Service
public class ProductService {
    private Repo repo;
    private ImageRepo imageRepo;
    private RedisTemplate<String, Object> redisTemplate;
    private static final String PRODUCT_INDEX_KEY = "product:index";
    private static final String PRODUCT_KEY_PREFIX = "product::";


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
        Long totalIds = redisTemplate.opsForZSet().size(PRODUCT_INDEX_KEY);
        long totalProducts = repo.count();
        if(totalIds == totalProducts){
            Set<Object> ids = redisTemplate.opsForZSet().range(PRODUCT_INDEX_KEY, start, end);
            List<String> keys = ids.stream()
                    .map(id -> PRODUCT_KEY_PREFIX + id.toString())
                    .toList();
            List<Product> products = redisTemplate.opsForValue().multiGet(keys).stream()
                    .map(obj -> (Product)obj)
                    .toList();

            return new PageImpl<Product>(products, PageRequest.of(currentPage, pageSize), totalProducts);
        }else {
            Page<Product> page = repo.findAll(PageRequest.of(currentPage,pageSize));
            page.forEach(p -> redisTemplate.opsForZSet().add(PRODUCT_INDEX_KEY, p.getId(), p.getId()));
            page.forEach(p -> redisTemplate.opsForValue().set(PRODUCT_KEY_PREFIX+p.getId(), p));
            return page;
        }
    }

    @Cacheable(value = "product", key = "#id")
    public Product findProduct(int id){
        return repo.findById(id).orElse(null);
    }

    @CachePut(value = "product", key = "#product.id")
    public Product addProduct(Product product, MultipartFile multipartImage) throws IOException {
        Image image = new Image();
        image.setImageData(multipartImage.getBytes());
        image.setImageType(multipartImage.getContentType());
        image.setImageName(multipartImage.getOriginalFilename());
        image.setProduct(product);
        redisTemplate.opsForZSet().add(PRODUCT_INDEX_KEY, product.getId(), product.getId());
        imageRepo.save(image);
        return repo.save(product);
    }


    public byte[] getImageById(int id) {
        String encodedImage = (String)redisTemplate.opsForValue().get("image::" + id);
        byte[] decodedImage;
        if(encodedImage != null){
            decodedImage = Base64.getDecoder().decode(encodedImage);
        }else {
            decodedImage = imageRepo.findByProductId(id).getImageData();
            redisTemplate.opsForValue().set("image::" + id, decodedImage, Duration.ofDays(1));
        }

        return decodedImage;

    }

    public void updateProduct(int id, Product product, MultipartFile multipartImage) throws IOException {
        Image image = new Image();
        image.setImageName(multipartImage.getOriginalFilename());
        image.setImageData(multipartImage.getBytes());
        image.setImageType(multipartImage.getContentType());
        image.setProduct(product);
        redisTemplate.opsForValue().set("product::"+id, product, Duration.ofDays(1));
        redisTemplate.opsForValue().set("image::"+id, multipartImage.getBytes(), Duration.ofDays(1));
        repo.save(product);
        imageRepo.save(image);
    }

    @Caching(evict = {@CacheEvict(value = "product", key = "#id"), @CacheEvict(value = "image", key = "#id")})
    public void deleteProduct(int id) {
        repo.deleteById(id);
        imageRepo.deleteByProductId(id);
        redisTemplate.opsForZSet().remove(PRODUCT_INDEX_KEY, id);
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
