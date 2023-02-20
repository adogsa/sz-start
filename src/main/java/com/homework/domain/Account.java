package com.homework.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String userId;
    @JsonIgnore
    private String password;
    @JsonIgnore
    private String regNo;

    @ManyToMany
    private List<Role> roles = new ArrayList<>();

    private String refreshToken;

    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }

    public void setPassword(String encPassword) {
        this.password = encPassword;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
}
