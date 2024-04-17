package com.example.community.entity;


import com.example.community.domain.member.Role;
import com.example.community.dto.MemberDto;
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
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column
    private String email;

    @Column
    private String userpw;

    @Column
    private String usernick;

    @Column
    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public Member(Long id,String email,String userpw,String usernick,Role role){
        this.id = id;
        this.email =email;
        this.usernick = usernick;
        this.userpw =userpw;
        this.role = role;
    }
    public MemberDto toDto() {
        return MemberDto.builder().id(id).role(role).email(email).userpw(userpw).usernick(usernick).build();
    }


}
