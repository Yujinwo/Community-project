package com.example.community.dto;

import com.example.community.domain.member.Role;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
// 유저 회원가입 요청 및 가입 dto
@Getter
@Builder
@AllArgsConstructor
public class MemberDto {

    private Long id;
    // 권한 Enum
    private Role role;
    @Size(min=8,max=320,message = "이메일은 8~320자 이내로 작성해주세요")
    private String email;
    @Size(min=10,max=15,message = "비밀번호는 10~15자 이내로 작성해주세요")
    private String userpw;
    @Size(min=3,max=20,message = "닉네임은 3~20자 이내로 작성해주세요")
    private String usernick;
    public Member toEntity(){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return Member.builder()
                .email(email)
                // 패스워드 암호화
                .userpw(passwordEncoder.encode(userpw))
                .usernick(usernick)
                // User 권한 설정
                .role(role.USER)
                // 쪽지 거부 설정
                .noteblockd(false)
                .social("normal")
                .temporaryblockdate(LocalDateTime.now())
                .build();
    }
}
