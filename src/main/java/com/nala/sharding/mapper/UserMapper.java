package com.nala.sharding.mapper;

import com.nala.sharding.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author liansr@nala.com.cn
 * @since 2020-09-08
 */
@Repository
public interface UserMapper extends BaseMapper<User>{

    void save(@Param("user") User user);
}
