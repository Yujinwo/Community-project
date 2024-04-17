package com.example.community.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Role {
    USER, ADMIN;

    public static List<Role> getAllRoles() {
        return Arrays.asList(values());
    }
}
