package com.w.srb.oss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author xin
 * @date 2022-10-11-16:09
 */
@SpringBootApplication
@ComponentScan({"com.w.srb","com.w.common"})
public class serviceOssApplication {
    public static void main(String[] args) {
        SpringApplication.run(serviceOssApplication.class, args);
    }
}
