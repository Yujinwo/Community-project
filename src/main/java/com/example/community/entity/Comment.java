package com.example.community.entity;

import com.example.community.dto.CommentResponseDto;
import com.example.community.dto.MyArticleResponseDto;
import com.example.community.dto.MyCommentResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper=true)
public class Comment extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // 댓글 내용
    @Column(nullable = false)
    private String content;
    // Member Entity 다:1 관계 설정 * 한 유저가 여러 댓글이 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;
    // Article Entity 다:1 관계 설정 * 한 게시글안에 여러 댓글이 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Article article;
    // Comment Entity 다:1 관계 설정 * 한 댓글 안에 여러 댓글이 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;
    // 댓글 번호
    @Column
    private Long commentnumber;

    @Column
    private Long commentorder;
    // 댓글 깊이
    @Column
    private int redepth;
    // Comment Entity 1:다 관계 설정 * 한 댓글안에 여러 대댓글이 가능
    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Comment> child;

    // 대댓글 있는 상태에서 부모 댓글 삭제 여부
    @Builder.Default
    @Column
    private boolean deleted = false;
    // Entity -> CommentResponseDto 생성
    public CommentResponseDto toDto() {
        return CommentResponseDto.builder().id(id).content(content).member(member).article(article).createdDate(getCreatedDate()).modifiedDate(getModifiedDate()).child(child).parent(parent).commentnumber(commentnumber).redepth(redepth).deleted(deleted).build();
    }

    public MyCommentResponseDto changeMyCommentResponseDto() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String createdFormatDate = createdDate.format(formatter);
        return MyCommentResponseDto.builder().article_id(article.getId()).article_title(article.getTitle()).content(content).createdDate(createdFormatDate).build();
    }




}
