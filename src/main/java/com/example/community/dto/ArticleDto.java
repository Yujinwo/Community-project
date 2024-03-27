package com.example.community.dto;


import com.example.community.entity.Article;
import lombok.*;

@Getter
@NoArgsConstructor
public class ArticleDto {
    private Long id;
    private String title;
    private String content;
    private String nickname;

    @Builder    // 생성자를 만든 후 그 위에 @Builder 애노테이션 적용
    public ArticleDto(Long id, String title, String content,String nickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;

    }

    public Article toEntity() {
        return Article.builder().title(title).content(content).nickname(nickname).build();
    }

    public Article id_toEntity() {
        return Article.builder().id(id).title(title).content(content).nickname(nickname).build();
    }


}

