package com.example.community.dto;


import com.example.community.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDto {

    private Long id;
    private Member writer;
    private String message;
    private String createdDate;

}
