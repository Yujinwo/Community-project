package com.example.community.entity;


import com.example.community.domain.member.Role;
import com.example.community.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@BatchSize(size = 100)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    // 이메일
    @Column
    private String email;
    // 비밀번호
    @Column
    private String userpw;
    // 닉네임
    @Column(unique = true)
    private String usernick;
    // 유저 권한
    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column
    private Boolean noteblockd;

    public void changeNoteBlockd(Boolean noteblockd) {
        this.noteblockd = noteblockd;
    }
    public void changeUserNick(String usernick) {
        this.usernick = usernick;
    }
    public void changeUserPw(String userpw) {
     this.userpw = userpw;

    }

    @Column
    private LocalDateTime temporaryblockdate;
    // Entity -> MemberDto 생성
    public MemberDto toDto() {
        return MemberDto.builder().id(id).role(role).email(email).userpw(userpw).usernick(usernick).build();
    }


}
