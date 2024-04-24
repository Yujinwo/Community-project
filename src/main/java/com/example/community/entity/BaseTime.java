package com.example.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTime {
    // 작성 시간 생성
    @CreatedDate
    @Column(nullable = false,updatable = false)
    protected LocalDateTime createdDate;
    // 수정 시간 갱신
    @LastModifiedDate
    @Column(nullable = false)
    protected LocalDateTime modifiedDate;
}
