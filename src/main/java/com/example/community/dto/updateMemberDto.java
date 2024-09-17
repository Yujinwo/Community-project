package com.example.community.dto;



import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class updateMemberDto {
    private String usernick;
    private String userpw;
    private String confirmuserpw;
}
