package com.yicoder.mybatis;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.PageRowBounds;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

/**
 * @ProjectName: testDemo
 * @Package: com.suneee.mybatis
 * @ClassName: ${TYPE_NAME}
 * @Description: java类作用描述
 * @Author: 作者姓名
 * @CreateDate: 2018/5/31 10:08
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/5/31 10:08
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * Copyright: Copyright (c) 2018
 */
public class TestMyBatis {
    @Test
    public void test1() {
        String provinceid = "00";
        Integer pageNo = -1;
        Integer pageSize = 10;
        pageNo = pageNo == null ? 1 : pageNo;
        pageSize = pageSize == null ? 10 : pageSize;

        //用PageInfo对结果进行包装
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactoryBuilder ssfBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = ssfBuilder.build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        PubProvinceDAO pubProvinceDAO = sqlSession.getMapper(PubProvinceDAO.class);
        PageHelper.startPage(pageNo, pageSize);
        List<PubProvincesDO> pubProvincesDOS = pubProvinceDAO.selectPorvince(provinceid);
        //用PageInfo对结果进行包装
        PageInfo<PubProvincesDO> page = new PageInfo<PubProvincesDO>(pubProvincesDOS);
        //测试PageInfo全部属性
        System.out.println(page.getPageNum());
        System.out.println(page.getPageSize());
        System.out.println(page.getStartRow());
        System.out.println(page.getEndRow());
        System.out.println(page.getTotal());
        System.out.println(page.getPages());
        System.out.println(page.getFirstPage());
        System.out.println(page.getLastPage());
        System.out.println(page.isHasPreviousPage());
        System.out.println(page.isHasNextPage());

    }

    @Test
    public void test2() {
        //用PageInfo对结果进行包装
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactoryBuilder ssfBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = ssfBuilder.build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        PubProvinceDAO pubProvinceDAO = sqlSession.getMapper(PubProvinceDAO.class);
        String provinceid = "00";
        PageRowBounds rowBounds = new PageRowBounds(4, 10);
        List<PubProvincesDO> pubProvincesDOS = pubProvinceDAO.selectPorvinceByRowBounds(provinceid, rowBounds);
        System.out.println(pubProvincesDOS);
        System.out.println(rowBounds.getTotal());
    }

    @Test
    public void test3() {
        //用PageInfo对结果进行包装
        Reader reader = null;
        try {
            reader = Resources.getResourceAsReader("mybatis-config.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactoryBuilder ssfBuilder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sqlSessionFactory = ssfBuilder.build(reader);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        PubProvinceDAO pubProvinceDAO = sqlSession.getMapper(PubProvinceDAO.class);
        String provinceid = "00";
        PageHelper.offsetPage(30, 10);
        List<PubProvincesDO> pubProvincesDOS = pubProvinceDAO.selectPorvinceByOffsetPage(provinceid);
        System.out.println(pubProvincesDOS);


    }
}



