package com.example.community.entity;

import com.example.community.dto.ArticleDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.util.IdGenerator;

@Entity
@Getter
@NoArgsConstructor
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column
    private String content;

    @Column
    private String nickname;

    @Builder    // 생성자를 만든 후 그 위에 @Builder 애노테이션 적용
    public Article(Long id , String title, String content,String nickname) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;

    }

    public ArticleDto toDto() {
        return ArticleDto.builder().id(id).title(title).content(content).nickname(nickname).build();
    }







}
