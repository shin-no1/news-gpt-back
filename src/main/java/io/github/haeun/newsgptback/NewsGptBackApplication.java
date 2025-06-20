package io.github.haeun.newsgptback;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEncryptableProperties
@SpringBootApplication
public class NewsGptBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsGptBackApplication.class, args);
    }

}
