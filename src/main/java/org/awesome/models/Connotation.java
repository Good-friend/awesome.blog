package org.awesome.models;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_connotation")
public class Connotation {
    private int id;
    private String serialNumber;//问题编号
    private String content;

    private String contentOriginal;//未转为html的markdown语法

}
