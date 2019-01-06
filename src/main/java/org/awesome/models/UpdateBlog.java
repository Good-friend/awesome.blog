package org.awesome.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlog {

    private String updateDate;

    private String updateContent;

    private String technology;

    private String version;


    public String validate(){
        if(StringUtils.isEmpty(version)){
            return "更新版本号你得填吧";
        }
        if(StringUtils.isEmpty(updateContent)){
            return "更新内容你得写吧";
        }
        return "1";
    }
}
