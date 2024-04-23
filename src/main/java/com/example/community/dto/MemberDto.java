package com.example.community.dto;

import com.example.community.domain.member.Role;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
public class MemberDto {

    private Long id;
    private Role role;

    @Size(min=8,max=320,message = "이메일은 8~320자 이내로 작성해주세요")
    private String email;

    @Size(min=10,max=15,message = "비밀번호는 10~15자 이내로 작성해주세요")
    private String userpw;

    @Size(min=3,max=20,message = "닉네임은 3~20자 이내로 작성해주세요")
    private String usernick;

    @Builder
    public MemberDto(Long id, Role role, String email, String userpw, String usernick){
        this.id =id;
        this.role =role;
        this.email = email;
        this.userpw = userpw;
        this.usernick =usernick;
    }

    public Member toEntity(PasswordEncoder passwordEncoder){
        return Member.builder()
                .email(email)
                .userpw(passwordEncoder.encode(userpw))
                .usernick(usernick)
                .role(role.USER)
                .build();
    }
}
