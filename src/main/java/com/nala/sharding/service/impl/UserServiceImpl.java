package com.nala.sharding.service.impl;

import com.nala.sharding.domain.User;
import com.nala.sharding.mapper.UserMapper;
import com.nala.sharding.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author liansr@nala.com.cn
 * @since 2020-09-08
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
