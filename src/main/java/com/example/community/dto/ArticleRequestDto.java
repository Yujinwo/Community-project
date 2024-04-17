package com.example.community.dto;

import com.example.community.entity.Article;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ArticleRequestDto {

    private Long id;

    @Size(min=1,max=60,message = "제목은 1~60자 이내로 작성해주세요")
    private String title;

    @Size(min=1,max=1000,message = "내용은 1~1000자 이내로 작성해주세요")
    private String content;

    private Member member;

    public Article toEntity() {

        return Article.builder().title(title).content(content).memberEntity(member).build();
    }


}
