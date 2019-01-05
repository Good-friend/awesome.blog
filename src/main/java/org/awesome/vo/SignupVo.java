package org.awesome.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
@Getter
public class SignupVo {
    private String username;

    private String nickname;

    private String sex;
    private String email;

    private String vercode;


    public String validate(){
        if(StringUtils.isEmpty(username)){
            return "用户名为空";
        }
        if(StringUtils.isEmpty(nickname)){
            return "昵称为空";
        }
        if(StringUtils.isEmpty(sex)){
            return "性别为空";
        }
        if(StringUtils.isEmpty(email)){
            return "邮箱为空";
        }
        if(StringUtils.isEmpty(vercode)){
            return "验证码为空";
        }
        return "1";
    }
}
