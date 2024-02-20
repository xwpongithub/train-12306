package com.jiawa.train.business.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jiawa.train.business.entity.SkToken;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.Date;

@Mapper
public interface SkTokenMapper extends BaseMapper<SkToken> {

    @Update("""
          update public.sk_token
          set count = CASE WHEN count < #{decreaseCount} THEN 0 ELSE count - #{decreaseCount} END
          where date = #{date}
          and train_code = #{trainCode}
          and count > 0
    """)
    int decrease(@Param("date") Date date,@Param("trainCode") String trainCode,
                 @Param("decreaseCount") int decreaseCount);

}
