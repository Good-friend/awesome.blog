package org.awesome.models;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 大厅目录表
 */
@Setter
@Getter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@TableName("t_catalogue")
public class Catalogue {

    private int id;

    @TableField("serial_number")
    private String serialNumber;//问题编号

    private String title;// 标题

    private String status;// 问题状态
    // 0-未结；1-已结

    @TableField("create_time")
    private String createTime;//		创建时间

    @TableField("end_time")
    private String endTime;//		结束时间

    @TableField("seenTimes")
    private int seen_times;//浏览次数

    @TableField("commentTimes")
    private int comment_times;//评价次数

    private boolean best;//是否是精帖

    private String author;//作者---username

    private String type;//类型：具体看枚举TypeEnum

    private boolean stick;//是否置顶



}
