package org.awesome.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 用于页面缓存的客户信息
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserTempVo {

    private String username;

    private String nickname;

    private String headPortraitUrl;

    private String authority;
}
