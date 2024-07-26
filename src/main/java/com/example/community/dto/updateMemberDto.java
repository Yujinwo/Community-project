package com.example.community.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class updateMemberDto {
    private String usernick;
    private String userpw;
    private String confirmuserpw;
}
