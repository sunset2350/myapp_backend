package com.pgc.myapp.profile;

import com.pgc.myapp.auth.entity.Login;
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

    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String userId;
    private String userPhone;
    private String userBirth;


    @OneToOne
    private Login login;
}
