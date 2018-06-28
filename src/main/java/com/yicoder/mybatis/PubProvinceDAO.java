package com.yicoder.mybatis;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * @ProjectName: testDemo
 * @Package: com.suneee.mybatis
 * @ClassName: ${TYPE_NAME}
 * @Description: java类作用描述
 * @Author: 作者姓名
 * @CreateDate: 2018/5/31 9:56
 * @UpdateUser: Neil.Zhou
 * @UpdateDate: 2018/5/31 9:56
 * @UpdateRemark: The modified content
 * @Version: 1.0
 * Copyright: Copyright (c) 2018
 */
public interface PubProvinceDAO {

    List<PubProvincesDO> selectPorvince(@Param("provinceid")String provinceid);
    List<PubProvincesDO> selectPorvinceByRowBounds(@Param("provinceid")String provinceid, RowBounds rowBounds);
    List<PubProvincesDO> selectPorvinceByOffsetPage(@Param("provinceid")String provinceid);
}
