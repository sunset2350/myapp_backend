package com.pgc.myapp.auth;


import com.pgc.myapp.auth.entity.Login;
import com.pgc.myapp.auth.entity.LoginRepository;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
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
    public ResponseEntity signUp(@RequestBody SignupRequest req){

        long profileNo = service.createIdentity(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileNo);
    }

    @PostMapping(value = "/sign")
    public ResponseEntity signIn(
            @RequestParam String userId,
            @RequestParam String userPw,
            HttpServletResponse res)
    {
        Optional<Login> login = loginRepository.findByUserId(userId);
        if(!login.isPresent()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        boolean isVerified = hashUtil.verifyHash(userPw,login.get().getUserPw());
        if(!isVerified) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Login l = login.get();

        Optional<Profile> profile = profileRepository.findByLogin_No(l.getNo());
        if(!profile.isPresent()){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        String token = jwtUtil.createToken(
                l.getNo(),l.getUserId(),
                profile.get().getUserName());

        Cookie cookie =new Cookie("token",token);
        cookie.setPath("/");
        cookie.setMaxAge((int) (jwtUtil.TOKEN_TIMEOUT / 1000L));
        cookie.setDomain("localhost");

        res.addCookie(cookie);


        return ResponseEntity
                .status(HttpStatus.FOUND)
                .location(ServletUriComponentsBuilder
                        .fromHttpUrl("http://localhost:5500")
                        .build().toUri())
                .build();
    }
}

