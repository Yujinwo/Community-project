package com.example.community.controller;

import com.example.community.dto.MemberDto;
import com.example.community.dto.NoteResponseDto;
import com.example.community.entity.Member;
import com.example.community.entity.Note;
import com.example.community.repository.MemberRepository;
import com.example.community.repository.NoteRespository;
import com.example.community.service.NoteService;
import com.example.community.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NoteController {

    private final NoteService noteService;
    private final NoteRespository noteRespository;
    private final MemberRepository memberRepository;
    private final AuthenticationUtil authenticationUtil;

    @GetMapping("/message")
    public String sendMessageWindow(@RequestParam("id") String email, Model model) {
        model.addAttribute("email",email);
        return "sendmessage";
    }
    @GetMapping("/receivemessage/{id}")
    public String receiveMessageWindow(@PathVariable("id") Long id, String email, Model model) {
        Optional<Note> savednote = noteRespository.findById(id);

        if(savednote.isPresent()){
            model.addAttribute("note",savednote.get());
        }
        return "receivemessage";
    }

    @GetMapping("/note")
    public String findNoteList(Pageable pageable, Model model) {
        Optional<Member> byId = memberRepository.findById(authenticationUtil.getCurrentMember().getId());
        if(byId.isPresent()) {
           model.addAttribute("member",byId.get());
        }
        Page<NoteResponseDto> notelists = noteService.findNotes(pageable).map(Note::changeNoteDto);
        // 페이지 최대 개수 설정
        int blockLimit = 3;
        // 시작 페이지
        int startPage = Math.max(1, notelists.getPageable().getPageNumber() - blockLimit);
        // 마지막 페이지
        int endPage = Math.min(notelists.getPageable().getPageNumber()+4, notelists.getTotalPages());
        model.addAttribute("notelists",notelists);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);


        return "note";
    }
}
