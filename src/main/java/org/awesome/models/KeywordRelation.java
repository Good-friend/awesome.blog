package org.awesome.models;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_keyword_relation")
public class KeywordRelation {
    private int id;

    private String keywordName;

    private String keywordRelation;


}
