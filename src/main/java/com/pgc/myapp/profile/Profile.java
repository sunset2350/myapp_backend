package com.pgc.myapp.profile;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long no;

    @Column(nullable = false, unique = true)
    private String userId;
    @Column(nullable = false)
    private String userPw;
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String userPhone;
    private String userBirth;
}
