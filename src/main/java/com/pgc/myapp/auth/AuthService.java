package com.pgc.myapp.auth;


import com.pgc.myapp.auth.entity.Login;
import com.pgc.myapp.auth.entity.LoginRepository;
import com.pgc.myapp.auth.util.HashUtil;
import com.pgc.myapp.profile.Profile;
import com.pgc.myapp.profile.ProfileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private LoginRepository loginRepository;
    private ProfileRepository profileRepository;

    @Autowired
    public AuthService(LoginRepository loginRepository,
    ProfileRepository profileRepository){
        this.loginRepository = loginRepository;
        this.profileRepository = profileRepository;
    }

    @Transactional
    public long createIdentity(SignupRequest req){
        HashUtil util = new HashUtil();

        Login toSaveLogin =
                Login.builder()
                        .userId(req.getUserId())
                        .userPw(util.createHash(req.getUserPw()))
                        .build();

        Login savedLogin = loginRepository.save(toSaveLogin);

        Profile toSaveProfile =
                Profile.builder()
                        .userName(req.getUserName())
                        .userPhone(req.getUserPhone())
                        .userBirth(req.getUserBirth())
                        .login(savedLogin)
                        .build();

        long profileNo = profileRepository.save(toSaveProfile).getNo();
        System.out.println(profileNo);
        savedLogin.setProfileNo(profileNo);
        loginRepository.save(savedLogin);

        return profileNo;
    }
}
