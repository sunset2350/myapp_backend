package com.pgc.myapp.auth;


import com.pgc.myapp.auth.entity.Login;
import com.pgc.myapp.auth.entity.LoginRepository;
import com.pgc.myapp.auth.request.SignupRequest;
import com.pgc.myapp.auth.util.HashUtil;
import com.pgc.myapp.profile.Profile;
import com.pgc.myapp.profile.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private LoginRepository loginRepository;
    private ProfileRepository profileRepository;

    @Autowired
    private HashUtil hashUtil;

    @Autowired
    public AuthService(LoginRepository loginRepository,
    ProfileRepository profileRepository){
        this.loginRepository = loginRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public long createIdentity(SignupRequest req){
        Login toSaveLogin =
                Login.builder()
                        .userId(req.getUserId())
                        .userPw(hashUtil.createHash(req.getUserPw()))
                        .build();

        Login savedLogin = loginRepository.save(toSaveLogin);

        Profile toSaveProfile =
                Profile.builder()
                        .userName(req.getUserName())
                        .userPhone(req.getUserPhone())
                        .userBirth(req.getUserBirth())
                        .userId(req.getUserId())
                        .userPw(hashUtil.createHash(req.getUserPw()))
                        .login(savedLogin)
                        .build();

        long profileNo = profileRepository.save(toSaveProfile).getNo();
        savedLogin.setAuthProfileNo(profileNo);
        loginRepository.save(savedLogin);

        return profileNo;
    }
    @Transactional
    public void changePassword(long no, String password){
        Login login = loginRepository.findByNo(no);
        login.setUserPw(hashUtil.createHash(password));
        loginRepository.save(login);
    }
}
