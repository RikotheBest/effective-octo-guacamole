package com.example.springboot_webapp.controller;

import com.example.springboot_webapp.model.Product;
import com.example.springboot_webapp.model.User;
import com.example.springboot_webapp.service.JwtService;
import com.example.springboot_webapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;


import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/api")
public class Controller {
    private ProductService service;
    private AuthenticationManager authenticationManager;
    private JwtService jwtService;


    @Autowired
    public Controller(ProductService service, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.service = service;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;

    }

    public Controller() {
    }
    @GetMapping("/products")
    public ResponseEntity<?> getProducts(@RequestParam(required = false) Integer currentPage, @RequestParam(required = false) Integer pageSize){
        if(currentPage == null && pageSize == null) return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
        return new ResponseEntity<>(service.findAll(currentPage, pageSize), HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable int id){
        Product product = service.findProduct(id);
        if(product == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(product,HttpStatus.OK);
    }
    @PostMapping(path = "/product", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> addProduct(@RequestPart("product") Product product, @RequestParam("imageFile") MultipartFile multipartImage) {
        if(product == null && multipartImage == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        try {
            service.addProduct(product, multipartImage);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/filter")
    public ResponseEntity<Page<Product>> filter(@RequestParam String category, @RequestParam int currentPage, @RequestParam int pageSize){
        return new ResponseEntity<>(service.filter(category, currentPage, pageSize), HttpStatus.OK);
    }
    @GetMapping("/product/{id}/image")
    public ResponseEntity<byte[]> getImage(@PathVariable int id ){
        try {
            byte[] image = service.getImageById(id);
            return new ResponseEntity<>(image,HttpStatus.OK);
        }catch (NullPointerException e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    @PutMapping(path = "/product/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> updateProduct(@RequestPart("product") Product product, @RequestParam(value = "imageFile") MultipartFile multipartImage, @PathVariable int id ){
        if(product == null && multipartImage == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        try {
            service.updateProduct(id, product, multipartImage);

            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/product/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable int id){
        service.deleteProduct(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchForProduct(@RequestParam String keyword){
        List<Product> products = service.searchFor(keyword);
//        if(products.size() == 0) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
    @PostMapping("/login")
    public ResponseEntity<String> authenticate(@RequestBody User user){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if(authentication.isAuthenticated()){
            return new ResponseEntity<>(jwtService.generateToken(user.getUsername()), HttpStatus.OK);
        }
        else return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

}
