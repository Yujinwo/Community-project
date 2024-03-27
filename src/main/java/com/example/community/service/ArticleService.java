package com.example.community.service;

import com.example.community.dto.ArticleDto;
import com.example.community.entity.Article;
import com.example.community.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class ArticleService {
    @Autowired
   ArticleRepository articleRepository;

    public Article write(ArticleDto articleDto) {
       return articleRepository.save(articleDto.toEntity());
    }


    public Page<ArticleDto> index(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작한다.
        int pageLimit = 3; // 한페이지에 보여줄 글 개수
        Page<ArticleDto> articleDtos = articleRepository.findAll(PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.ASC, "id"))).map(Article::toDto);
        System.out.println(articleDtos);
        return articleDtos;
    }

    public void delete(ArticleDto articleDto) {
        articleRepository.delete(articleDto.id_toEntity());

    }

    public Article update(ArticleDto articleDto) {

        return articleRepository.save(articleDto.id_toEntity());
    }
}
