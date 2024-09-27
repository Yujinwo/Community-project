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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@BatchSize(size = 100)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column
    private String email;
    @Column
    private String userpw;
    @Column
    private String usernick;
    // 유저 권한
    @Column
    @Enumerated(EnumType.STRING)
    private Role role;
    // 쪽지 수신 거부
    @Column
    private Boolean noteblockd;

    @Column
    private String social;

    public void changeNoteBlockd(Boolean noteblockd) {
        this.noteblockd = noteblockd;
    }
    public void changeUserNick(String usernick) {
        this.usernick = usernick;
    }
    public void changeUserPw(String userpw) {
        this.userpw = userpw;
    }
    // 임시 거부 유효시간
    @Column
    private LocalDateTime temporaryblockdate;

    // Entity -> MemberDto 생성
    public MemberDto toDto() {
        return MemberDto.builder().id(id).role(role).email(email).userpw(userpw).usernick(usernick).build();
    }


}
