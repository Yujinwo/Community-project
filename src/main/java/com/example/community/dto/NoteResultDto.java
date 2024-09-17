package com.example.community.dto;


import com.example.community.entity.Member;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteResultDto {

    private List<NoteResponseDto> content;
    private int totalPages;
    private Boolean hasResult;
    private Boolean last;
    private Boolean first;
    private Boolean previous;
    private Boolean next;
    private int number;
    public static NoteResultDto createNoteResultDto(Page<NoteResponseDto> notelists) {
        return NoteResultDto.builder().first(notelists.isFirst()).last(notelists.isLast()).hasResult(notelists.hasContent()).previous(notelists.hasPrevious()).next(notelists.hasNext()).content(notelists.getContent()).number(notelists.getNumber()).totalPages(notelists.getTotalPages()).build();
    }

}
