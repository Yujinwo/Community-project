package com.example.community.entity;

import com.example.community.dto.CommentResponseDto;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
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
    @ManyToOne
    @JoinColumn(name = "userid")
    private Member member;
    // Article Entity 다:1 관계 설정 * 한 게시글안에 여러 댓글이 가능
    @ManyToOne
    @JoinColumn(name = "boardid")
    private Article article;
    // Reply Entity 1:다 관계 설정 * 한 댓글안에 여러 대댓글이 가능

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Reply> child;
    // 대댓글 있는 상태에서 부모 댓글 삭제 여부
    @Builder.Default
    @Column
    private boolean deleted = false;
    // Entity -> CommentResponseDto 생성
    public CommentResponseDto toDto() {
        return CommentResponseDto.builder().id(id).content(content).member(member).article(article).createdDate(getCreatedDate()).modifiedDate(getModifiedDate()).child(child).deleted(deleted).build();

    }



}
