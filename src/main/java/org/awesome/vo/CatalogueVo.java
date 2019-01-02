package org.awesome.vo;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CatalogueVo {
    private int id;

    private String serialNumber;//编号

    private String title;// 标题

    private String status;// 问题状态// 0-未结；1-已结

    private String createTime;//		创建时间

    private String endTime;//		结束时间


    private int seenTimes;//浏览次数


    private int commentTimes;//评价次数

    private boolean best;//是否是精帖

    private boolean publicity;//是否公开

    private String author;//作者---username

    private String type;//类型：具体看枚举TypeEnum

    private boolean stick;//是否置顶

    private String nickName;

    private String headPortraitUrl;

    private String typeName;

    private String content;
}
