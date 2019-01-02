package org.awesome.models;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@TableName("t_user")
@Setter
@Getter
public class User {
    private String username;
    private String password;
    private String userStatus;
    private int loginTimes;
    private String nickname;
    private String headPortraitUrl;
    private String sex;
    private String city;
    private String createDate;
    private String description;
    @TableField(exist = false)
    private List<String> Authorities;

}
