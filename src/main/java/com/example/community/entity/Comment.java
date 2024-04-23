package com.example.community.entity;

import com.example.community.dto.CommentResponseDto;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Comment extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne
    @JoinColumn(name = "userid")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "boardid")
    private Article article;

    @OneToMany(mappedBy = "comment", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<Reply> child = new ArrayList<>();

    private boolean deleted;

    @Builder
    public Comment(Long id, String content, Member member, Article article, List<Reply> child, boolean deleted){
        this.id= id;
        this.content = content;
        this.member = member;
        this.article = article;
        this.child = child;
        this.deleted = deleted;

    }

    public CommentResponseDto toDto() {
        return CommentResponseDto.builder().id(id).content(content).member(member).article(article).createdDate(getCreatedDate()).modifiedDate(getModifiedDate()).child(child).deleted(deleted).build();

    }



}
