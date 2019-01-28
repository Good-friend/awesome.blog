package org.awesome.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 评论
 */
@Data
@Setter
@Getter
public class Comment {
    private String id;

    private String serialNumber;

    private String author;//--user的nickname

    private String username;

    private String replyContent;

    private String createTime;

    private int thumbUpTimes;//点赞次数

    private String authorHeadUrl;

    private String defendant;//被回复人username


}