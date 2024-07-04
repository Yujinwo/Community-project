package com.example.community.repository;

import com.example.community.entity.Notification;
import com.example.community.repository.querydsl.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Long>, UserRepositoryCustom {
}
