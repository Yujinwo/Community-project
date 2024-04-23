package com.example.community.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Reply extends BaseTime{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @ManyToOne
    @JoinColumn(name = "userid")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "commentid")
    private Comment comment;

    @Builder
    public Reply(Long id, String content, Member member, Comment comment){
        this.id = id;
        this.content = content;
        this.member = member;
        this.comment = comment;

    }


}
