package com.example.community.entity;

import com.example.community.dto.ArticleResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString

public class Article extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String title;

    @Column(length = 1000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "userid")
    private Member member;

    @Column
    private int viewcount;

    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<BoardImage> boardImages = new ArrayList<>();

    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<Comment> comments = new ArrayList<>();

    @Builder    // 생성자를 만든 후 그 위에 @Builder 애노테이션 적용
    public Article(Long id , String title, String content,Member memberEntity,int viewcount,LocalDateTime createdDate,List<BoardImage> boardImages) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = memberEntity;
        this.viewcount = viewcount;
        this.createdDate = createdDate;
        this.boardImages = boardImages;

    }

    public ArticleResponseDto toDto() {
        return ArticleResponseDto.builder().id(id).title(title).content(content).createdDate(createdDate).modifiedDate(modifiedDate).member(member).viewcount(viewcount).boardImages(boardImages).comments(comments).build();
    }

}
