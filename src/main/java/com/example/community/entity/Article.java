package com.example.community.entity;

import com.example.community.dto.ArticleResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper=true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Article extends BaseTime{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 글 제목
    @Column
    private String title;
    // 글 내용
    @Column(length = 1000)
    private String content;
    // Member Entity 다:1 관계 설정 * 한 유저가 여러 게시글이 가능
    @ManyToOne
    @JoinColumn(name = "userid")
    private Member member;
    // 조회수
    @Column
    private int viewcount;
    // boardImages Entity 1:다 관계 설정 *한 게시글 안에 여러 이미지가 가능
    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<BoardImage> boardImages;
    // comments Entity 1:다 관계 설정 * 한 게시글 안에 여러 댓글이 가능
    @OneToMany(mappedBy = "article", orphanRemoval = true, cascade = CascadeType.REMOVE)
    @OrderBy("id asc")
    private List<Comment> comments;
    // 댓글 수
    @Column
    private int commentcount;

    // Entity -> ArticleResponseDto 생성
    public ArticleResponseDto toDto() {
        return ArticleResponseDto.builder().id(id).title(title).content(content).createdDate(createdDate).modifiedDate(modifiedDate).member(member).viewcount(viewcount).boardImages(boardImages).comments(comments).commentcount(commentcount).build();
    }

}
