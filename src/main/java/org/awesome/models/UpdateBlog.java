package org.awesome.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlog {

    private String updateDate;

    private String updateContent;

    private String technology;

    private String version;
}
