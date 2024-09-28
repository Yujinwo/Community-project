package com.example.community.dto;



import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원정보 수정 요청 DTO
@Getter
@NoArgsConstructor
public class updateMemberDto {
    @Size(min=3,max=20,message = "닉네임은 3~20자 이내로 작성해주세요")
    private String usernick;
    @Size(min=10,max=15,message = "원본 비밀번호는 10~15자 이내로 작성해주세요")
    private String originaluserpw;
    @Size(min=10,max=15,message = "변경 비밀번호는 10~15자 이내로 작성해주세요")
    private String userpw;
    @Size(min=10,max=15,message = "재확인 비밀번호는 10~15자 이내로 작성해주세요")
    private String confirmuserpw;
}
