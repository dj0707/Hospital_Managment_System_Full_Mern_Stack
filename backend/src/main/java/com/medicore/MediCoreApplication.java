package com.medicore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
public class MediCoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(MediCoreApplication.class, args);
    }
}
