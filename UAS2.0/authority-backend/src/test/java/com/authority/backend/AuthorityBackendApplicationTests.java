package com.authority.backend;

import com.authority.backend.entity.SysUser;
import com.authority.backend.mapper.SysUserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class BackendApplicationTests {

    @Autowired
    private SysUserMapper sysUserMapper; // 注入我们刚才写的 Mapper

    @Test
    void testSelect() {
        System.out.println(("----- 开始测试数据库查询 ------"));
        // selectList(null) 表示查询所有，条件为 null
        List<SysUser> userList = sysUserMapper.selectList(null);

        // 打印结果
        userList.forEach(System.out::println);

        // 简单的断言，确保查到了数据
        assert userList.size() > 0;
        System.out.println("----- 测试结束，成功查到 " + userList.size() + " 条数据 ------");
    }

}