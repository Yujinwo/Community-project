package com.example.community.dto;

import com.example.community.entity.Article;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
public class ArticleRequestDto {

    private Long id;
    // 제목 사이즈 1~60자
    @Size(min=1,max=60,message = "제목은 1~60자 이내로 작성해주세요")
    private String title;
    // 내용 사이즈 1~1000자
    @Size(min=1,max=1000,message = "내용은 1~1000자 이내로 작성해주세요")
    private String content;
    // 멤버 Entity
    private Member member;
    // 이미지 Url
    private List<String> imageUrls;
    private List<String> tags;
    // dto -> Article Entity로 생성
    public Article toEntity() {
        return Article.builder().title(title).content(content).member(member).build();
    }

    public void changeMember(Member member) {
        this.member = member;
    }
}
