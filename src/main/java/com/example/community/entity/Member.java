package com.example.community.entity;


import com.example.community.domain.member.Role;
import com.example.community.dto.MemberDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long id;
    // 이메일
    @Column
    private String email;
    // 비밀번호
    @Column
    private String userpw;
    // 닉네임
    @Column
    private String usernick;
    // 유저 권한
    @Column
    @Enumerated(EnumType.STRING)
    private Role role;
    // Entity -> MemberDto 생성
    public MemberDto toDto() {
        return MemberDto.builder().id(id).role(role).email(email).userpw(userpw).usernick(usernick).build();
    }


}
