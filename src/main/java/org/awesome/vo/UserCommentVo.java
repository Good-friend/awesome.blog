package org.awesome.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserCommentVo {

    private String serialNumber;

    private String title;

    private String username;

    private String replyContent;

    private String createTime;

}
