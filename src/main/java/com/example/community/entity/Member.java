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
    private Boolean noteblock;

    @Column
    private String social;

    @Column
    private LocalDateTime temporaryBlockDate;

    public void changeNoteBlock(Boolean noteblock) {
        this.noteblock = noteblock;
    }
    public void changeTemporaryblockdate(LocalDateTime temporaryblockdate) {
        this.temporaryBlockDate = temporaryblockdate;
    }
    public void changeUserNick(String usernick) {
        this.usernick = usernick;
    }
    public void changeUserPw(String userpw) {
        this.userpw = userpw;
    }
    // 임시 거부 유효시간


    // Entity -> MemberDto 생성
    public MemberDto toDto() {
        return MemberDto.builder().id(id).role(role).email(email).userpw(userpw).usernick(usernick).build();
    }


}
