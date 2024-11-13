package com.example.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@NoArgsConstructor
public class Role {

    @Id
    private String username;
    private String authority;
}