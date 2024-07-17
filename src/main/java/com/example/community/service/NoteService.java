package com.example.community.service;


import com.example.community.dto.NoteSaveRequestDto;
import com.example.community.entity.Member;
import com.example.community.entity.Note;
import com.example.community.repository.NoteRespository;
import com.example.community.util.AuthenticationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final AuthenticationUtil authenticationUtil;
    private final NoteRespository noteRespository;
    public Note saveNote(Member byemail, String message) {

        Member member = authenticationUtil.getCurrentMember();
        if(member != null)
        {
            Note saveNote = Note.builder().receiver(byemail).writer(member).message(message).read(false).build();
            Note SavedNote = noteRespository.save(saveNote);
            return SavedNote;
        }
        else {
            return null;
        }

    }
    public Page<Note> findNotes(Pageable pageable){
        Member member = authenticationUtil.getCurrentMember();
        if(member != null)
        {
            Page<Note> notelists = noteRespository.findByNote(member,pageable);
            return notelists;
        }
        else {
            return null;
        }
    }
}
