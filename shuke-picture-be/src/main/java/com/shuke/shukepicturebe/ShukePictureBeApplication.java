package com.shuke.shukepicturebe;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@MapperScan("com.shuke.shukepicturebe.mapper")
@EnableAspectJAutoProxy(exposeProxy = true)
public class ShukePictureBeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShukePictureBeApplication.class, args);
    }

}
