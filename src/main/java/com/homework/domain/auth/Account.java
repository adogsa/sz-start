package com.homework.domain.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.homework.converter.CryptoStringConverter;
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
    @JsonIgnore
    private Long id;

    private String name;
    private String userId;
    @JsonIgnore
    private String password;

    @Convert(converter = CryptoStringConverter.class)
    @JsonIgnore
    private String regNo;

    @JsonIgnore
    @ManyToMany
    private List<Role> roles = new ArrayList<>();

    private String refreshToken;

    public void updateRefreshToken(String newToken) {
        this.refreshToken = newToken;
    }
}
