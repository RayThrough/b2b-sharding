package com.nala.sharding.controller;


import com.nala.sharding.domain.User;
import com.nala.sharding.mapper.UserMapper;
import com.nala.sharding.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author liansr@nala.com.cn
 * @since 2020-09-08
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @RequestMapping("/test")
    public void test(){
        User user = new User("测试22", 1);
        userMapper.insert(user);
    }
}
