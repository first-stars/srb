package com.w.srb.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xin
 * @date 2022-10-07-9:00
 */
@SpringBootApplication
@ComponentScan({"com.w.srb","com.w.common"})
@EnableTransactionManagement //事务处理
public class ServiceCoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceCoreApplication.class, args);
    }
}
