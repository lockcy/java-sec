package com.example.javasec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@SpringBootApplication
public class JavaSecApplication {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @RequestMapping("/exec")
    public String execPing(@RequestParam String poc) throws IOException {
        Process process = Runtime.getRuntime().exec(poc+" 127.0.0.1");
        return "success";
    }


    public static void main(String[] args) {
        SpringApplication.run(JavaSecApplication.class, args);
    }

}
