package com.pgc.myapp.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Login {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long no;

    @Column(unique = true)
    private String userId;
    @Column(nullable = false)
    private String userPw;

    private long authProfileNo;
}
