package com.example.community.dto;

import com.example.community.entity.Article;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class ArticleRequestDto {
    private Long id;
    @Size(min=1,max=60,message = "제목은 1~60자 이내로 작성해주세요")
    private String title;
    // 내용 사이즈 1~1000자
    @Size(min=1,max=1000,message = "내용은 1~1000자 이내로 작성해주세요")
    private String content;
    private List<String> ImageUrls = new ArrayList<>();
    private List<String> tags = new ArrayList<>();
}
