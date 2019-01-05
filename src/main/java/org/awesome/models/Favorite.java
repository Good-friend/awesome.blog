package org.awesome.models;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_favorite")
public class Favorite {
    public int Id;
    public String username;
    public String serialNumber;
}
