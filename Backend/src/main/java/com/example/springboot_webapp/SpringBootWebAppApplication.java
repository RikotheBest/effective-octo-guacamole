package com.example.springboot_webapp;




import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;



@SpringBootApplication
public class SpringBootWebAppApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(SpringBootWebAppApplication.class, args);


    }

}
