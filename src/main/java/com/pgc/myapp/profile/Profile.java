package com.pgc.myapp.profile;

import com.pgc.myapp.auth.entity.Login;
import com.pgc.myapp.diary.Diary;
import com.pgc.myapp.diary.DiaryController;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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
    private String userPhone;
    private String userBirth;
    private String userId;
    private String userPw;


    @OneToOne
    private Login login;

}
