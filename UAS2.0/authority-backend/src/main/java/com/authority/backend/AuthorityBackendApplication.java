package com.authority.backend;

import org.mybatis.spring.annotation.MapperScan; // 记得导入这个包
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.authority.backend.mapper") // 【新增】扫描 Mapper 文件夹
public class AuthorityBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthorityBackendApplication.class, args);
    }

}
