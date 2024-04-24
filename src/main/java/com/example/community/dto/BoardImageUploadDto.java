package com.example.community.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
public class BoardImageUploadDto {
    // 이미지 파일 리스트
    private List<MultipartFile> files;
}
