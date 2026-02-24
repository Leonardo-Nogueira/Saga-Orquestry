package org.leonardonogueira;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderServiceApplication {

    public static void main(String[] args) { // 'public' e 'String[] args' são essenciais
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}