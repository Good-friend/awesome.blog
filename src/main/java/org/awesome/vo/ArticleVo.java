package org.awesome.vo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
@Getter
public class ArticleVo {

    //public static String SUCCESS = "1";

    private String title;// 标题

    private String content;// 内容-html

    private String endTime;//结束时间 （问题话题有）（废弃）

    private boolean best = false;//是否是精帖

    private String username;

    private String type;//类型：具体看枚举TypeEnum（废弃）

    private boolean stick = false;//是否置顶

    private boolean publicity;//是否公开（废弃）

    private String contentOriginal;//未转为html的markdown语法
    //private String vercode;//验证码

    public String validateParams(){
        if(StringUtils.isEmpty(title)){
            return "标题为空";
        }
        if(StringUtils.isEmpty(content) || StringUtils.isEmpty(contentOriginal)){
            return "内容为空";
        }
        if(StringUtils.isEmpty(username)){
            return "用户信息为空";
        }

        return "1";
    }

}
