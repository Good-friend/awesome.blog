package org.awesome.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GuestReply {


    private String createTime;

    private String content;

    private String email;


    private String dealStatus;//处理状态：0-未处理；1-已处理

    private String username;
}
