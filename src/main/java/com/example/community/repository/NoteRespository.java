package com.example.community.repository;

import com.example.community.entity.Member;
import com.example.community.entity.Note;
import com.example.community.repository.querydsl.UserRepositoryCustom;
import com.example.community.repository.querydsl.impl.UserRepositoryCustomImpl;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NoteRespository extends JpaRepository<Note,Long>, UserRepositoryCustom {

}
