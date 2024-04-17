package com.example.community.entity;

import com.example.community.repository.ReplyRepositoty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Negative;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

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
    public Reply(Long id,String content,Member member,Comment comment){
        this.id = id;
        this.content = content;
        this.member = member;
        this.comment = comment;

    }


}
