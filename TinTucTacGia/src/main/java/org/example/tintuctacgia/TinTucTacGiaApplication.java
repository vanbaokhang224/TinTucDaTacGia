package org.example.tintuctacgia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TinTucTacGiaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TinTucTacGiaApplication.class, args);
    }

}
