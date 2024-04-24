package com.example.community.dto;

import com.example.community.domain.member.Role;
import com.example.community.entity.Member;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

@Getter
@Builder
@AllArgsConstructor
public class MemberDto {

    private Long id;
    // 권한 Enum
    private Role role;
    // 이메일 사이즈 8~320자
    @Size(min=8,max=320,message = "이메일은 8~320자 이내로 작성해주세요")
    private String email;
    // 비밀번호 사이즈 10~15자
    @Size(min=10,max=15,message = "비밀번호는 10~15자 이내로 작성해주세요")
    private String userpw;
    // 닉네임 사이즈 3~20자
    @Size(min=3,max=20,message = "닉네임은 3~20자 이내로 작성해주세요")
    private String usernick;
    // dto -> Member Entity 생성
    public Member toEntity(PasswordEncoder passwordEncoder){
        return Member.builder()
                .email(email)
                // 패스워드 암호화
                .userpw(passwordEncoder.encode(userpw))
                .usernick(usernick)
                // User 권한 설정
                .role(role.USER)
                .build();
    }
}
