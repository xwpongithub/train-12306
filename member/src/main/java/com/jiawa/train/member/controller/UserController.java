package com.jiawa.train.member.controller;

import com.jiawa.train.member.entity.Member;
import com.jiawa.train.member.mapper.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final MemberMapper memberMapper;

    @GetMapping("1")
    public List<Member> user1() {
        //参数1为终端ID
//参数2为数据中心ID
//        var snowflake = IdUtil.getSnowflake(1, 1);
//        long id = snowflake.nextId();
//        Db.use().insert(Entity.create("t_message")
//                .set("id",id)
//                .set("from_id",1)
//                .set("to_id",2)
//                .set("content","哈哈哈")
//                .set("sign_flag",0)
//                .set("create_time", DateUtil.date())
//        );
       return memberMapper.selectByExample(null);
    }

}
