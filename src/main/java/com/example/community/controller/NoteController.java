package com.example.community.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NoteController {

    @GetMapping("/message")
    public String sendMessageWindow() {
        return "message";
    }

    @GetMapping("/note")
    public String findNoteList() {
        return "note";
    }
}
