package com.example.community.dto;



import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 회원정보 수정 요청 DTO
@Getter
@NoArgsConstructor
public class updateMemberDto {
    private String originaluserpw;
    private String userpw;
    private String usernick;
    private String confirmuserpw;
}
