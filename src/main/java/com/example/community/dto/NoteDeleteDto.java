package com.example.community.dto;


import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class NoteDeleteDto {
    @Size(min = 1,max = 10,message = "최대 선택할 수 있는 개수는 10개입니다.")
    List<Long> selectdIds;
}
