package org.awesome.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 操作流水
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OperationFlow {

    private String action;//动作 1-查看；2-评论；3-点赞

    private String target;//目标：帖Id

    private String who; //username

    private String time; //时间

    private String ip; //IP地址

}
