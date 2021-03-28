package ru.ozhigov.kontur.intern.universalConverter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UniversalConverterApplication {
    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        SpringApplication.run(UniversalConverterApplication.class, args);
    }
}
