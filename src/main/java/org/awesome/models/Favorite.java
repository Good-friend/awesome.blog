package org.awesome.models;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.util.StringUtils;

@Data
@TableName("t_favorite")
public class Favorite {
    public int Id;

    public String username;

    public String serialNumber;

    public String createTime;

    public String validate(){
        if(StringUtils.isEmpty(username)){
            return "username is null";
        }
        if(StringUtils.isEmpty(serialNumber)){
            return "serialNumber is null";
        }
        return "1";
    }
}
