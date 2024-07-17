package com.example.community.dto;


import com.example.community.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteResponseDto {

    private Long id;
    private Member writer;
    private String message;
    private String createdDate;

}
