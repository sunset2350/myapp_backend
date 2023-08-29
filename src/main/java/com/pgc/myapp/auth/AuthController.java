package com.pgc.myapp.auth;


import com.pgc.myapp.auth.entity.Login;
import com.pgc.myapp.auth.entity.LoginRepository;
import com.pgc.myapp.auth.request.SignupRequest;
import com.pgc.myapp.auth.util.HashUtil;
import com.pgc.myapp.auth.util.JwtUtil;
import com.pgc.myapp.profile.Profile;
import com.pgc.myapp.profile.ProfileRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private LoginRepository loginRepository;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private AuthService service;

    @Autowired
    private HashUtil hashUtil;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/signup")
    public ResponseEntity signUp(@RequestBody SignupRequest req) {

        long profileNo = service.createIdentity(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileNo);
    }

    @PostMapping(value = "/sign")
    public ResponseEntity signIn(
            @RequestParam String userId,
            @RequestParam String userPw,
            HttpServletResponse res) {

        Optional<Login> login = loginRepository.findByUserId(userId);
        if (!login.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(ServletUriComponentsBuilder
                            .fromHttpUrl("http://localhost:5500/login.html?err=Unauthorized")
                            .build().toUri())
                    .build();
        }

        boolean isVerified = hashUtil.verifyHash(userPw, login.get().getUserPw());
        if (!isVerified) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(ServletUriComponentsBuilder
                            .fromHttpUrl("http://localhost:5500/login.html?err=Unauthorized")
                            .build().toUri())
                    .build();
        }

        Login l = login.get();

        Optional<Profile> profile = profileRepository.findByLogin_No(l.getNo());

        if (!profile.isPresent()) {
            return ResponseEntity
                    .status(HttpStatus.FOUND)
                    .location(ServletUriComponentsBuilder
                            .fromHttpUrl("http://localhost:5500?err=Conflict")
                            .build().toUri())
                    .build();
        }

        String token = jwtUtil.createToken(
                l.getNo(), l.getUserId(),
                profile.get().getUserName());
        System.out.println(token);


        Cookie cookie = new Cookie("token", token);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtUtil.TOKEN_TIMEOUT / 1000L));
        cookie.setDomain("localhost");

        res.addCookie(cookie);
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(ServletUriComponentsBuilder
                        .fromHttpUrl("http://localhost:5500/myapp_frontend")
                        .build().toUri())
                .build();
    }

    @GetMapping(value = "/find-Id")
    public Optional<Profile> findById(@RequestParam("userName") String userName,
                                      @RequestParam("userBirth") String userBirth) {

        return profileRepository.findByUserNameAndUserBirth(userName, userBirth);
    }

    @GetMapping(value = "find-Pw")
    public Optional<Profile> findByPw(@RequestParam("userName") String userName,
                                      @RequestParam("userId") String userId,
                                      @RequestParam("userPhone") String userPhone,
                                      @RequestBody SignupRequest req
                                      ) {
        Profile profile = new Profile();




        System.out.println(req);
        return profileRepository.findByUserIdAndUserPhoneAndUserName(userId, userPhone, userName);
    }
}

