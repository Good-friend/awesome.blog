package org.awesome.models;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Setter
@Getter
@TableName("t_user")
public class User {
    private String username;
    private String password;
    private String user_type;
    private String user_status;
    private int login_times;
    private String nick_name;
    @TableField(exist = false)
    private List<String> Authorities;

}
