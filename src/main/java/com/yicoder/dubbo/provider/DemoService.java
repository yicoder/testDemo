package com.yicoder.dubbo.provider;

import com.yicoder.dubbo.User;

import java.util.List;

/**
 * @Package:        com.suneee.dubbo
 * @ClassName:      DemoService
 * @Description:    TODO
 * @Author:         ziping
 * @CreateDate:     2018/5/14 10:55
 * @UpdateUser:     ziping
 * @UpdateDate:     2018/5/14 10:55
 * @UpdateRemark:   The modified content
 * @Version:        1.0
 * Copyright: Copyright (c) 2018/5/14
 */
public interface DemoService {
    String sayHello(String name);
    List<User> getUsers();
}

