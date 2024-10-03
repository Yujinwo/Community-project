package com.example.community.controller;

import com.example.community.dto.MemberDto;
import com.example.community.dto.NoteResponseDto;
import com.example.community.dto.NoteResultDto;
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
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;
    private final NoteRespository noteRespository;
    private final AuthenticationUtil authenticationUtil;
    // 쪽지 작성 페이지
    @GetMapping("/notes/new")
    public String sendMessageWindow(@RequestParam("id") String email, Model model, RedirectAttributes redirectAttributes) {
        Member user = authenticationUtil.getCurrentMember();
        // 유저 데이터가 없으면
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "유저 데이터가 존재하지 않습니다.");
            return "redirect:/"; // 홈으로 리다이렉트
        }
        model.addAttribute("email",email);
        return "send_note";
    }
    // 쪽지 상세 조회 페이지
    @GetMapping("/notes/{id}")
    public String receiveMessageWindow(@PathVariable("id") Long id, String email, Model model, RedirectAttributes redirectAttributes) {
        Member user = authenticationUtil.getCurrentMember();
        // 유저 데이터가 없으면
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "유저 데이터가 존재하지 않습니다.");
            return "redirect:/"; // 홈으로 리다이렉트
        }
        Optional<Note> savednote = noteRespository.findById(id);
        // 쪽지가 없으면
        if (savednote.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "쪽지 데이터가 존재하지 않습니다.");
            return "redirect:/"; // 홈으로 리다이렉트
        }
        model.addAttribute("note",savednote.get());
        return "receive_note";
    }

    // 쪽지 리스트 페이지
    @GetMapping("/notes")
    public String findNoteList(@PageableDefault(page = 1) Pageable pageable, Model model, RedirectAttributes redirectAttributes) {
        Member user = authenticationUtil.getCurrentMember();
        // 유저 데이터가 없으면
        if (user == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "유저 데이터가 존재하지 않습니다.");
            return "redirect:/"; // 홈으로 리다이렉트
        }
        // 내 쪽지 전체 페이징처리 조회
        NoteResultDto notelists = noteService.findNotes(pageable);
        // 최소 페이지
        int startPage = Math.max(1, notelists.getNumber() - 3);
        // 최대 페이지
        int endPage = Math.min(notelists.getNumber()+4, notelists.getTotalPages());

        model.addAttribute("notelists",notelists.getContent());
        model.addAttribute("member",authenticationUtil.getCurrentMember());
        model.addAttribute("pageable",notelists);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "note";
    }
}
