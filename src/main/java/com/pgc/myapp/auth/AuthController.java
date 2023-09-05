package com.pgc.myapp.auth;


import com.pgc.myapp.auth.entity.Login;
import com.pgc.myapp.auth.entity.LoginRepository;
import com.pgc.myapp.auth.request.SignupRequest;
import com.pgc.myapp.auth.util.HashUtil;
import com.pgc.myapp.auth.util.JwtUtil;
import com.pgc.myapp.diary.Diary;
import com.pgc.myapp.profile.Profile;
import com.pgc.myapp.profile.ProfileModifyRequest;
import com.pgc.myapp.profile.ProfileRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/auth")
@Tag(name="로그인 관리 API")
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
    @Operation(summary = "회원 가입")
    public ResponseEntity signUp(@RequestBody SignupRequest req) {
        String password = req.getUserPw();
        String id = req.getUserId();

        // 패스워드 정규표현식: 영어 + 숫자 + 특수문자 포함, 최소 8자 이상
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

        // ID 정규표현식: 영어 소문자, 대문자, 숫자만 허용
        String idPattern = "^[a-zA-Z0-9]+$";

        Pattern passwordPt = Pattern.compile(passwordPattern);
        Pattern idPt = Pattern.compile(idPattern);

        if (!idPt.matcher(id).matches()){
            System.out.println("ID 형식 오류");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("ID 형식이 맞지않음");

        }

        if (!passwordPt.matcher(password).matches()){
            System.out.println("패스워드 형식 오류");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("패스워드 형식이 맞지 않음");

        }

        long profileNo = service.createIdentity(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(profileNo);
    }

    @PostMapping(value = "/sign")
    @Operation(summary = "로그인")
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
                            .fromHttpUrl("http://localhost:5500")
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
                        .fromHttpUrl("http://localhost:5500/myapp_frontend/myapp_frontend")
                        .build().toUri())
                .build();
    }
    @Operation(summary = "아이디 찾기")
    @GetMapping(value = "/find-Id")
    public Optional<Profile> findById(@RequestParam("userName") String userName,
                                      @RequestParam("userBirth") String userBirth) {

        return profileRepository.findByUserNameAndUserBirth(userName, userBirth);
    }
    @Operation(summary = "패스워드 찾기")
    @PutMapping(value = "find-Pw")
    public ResponseEntity findByPw(@RequestParam("userName") String userName,
                                   @RequestParam("userId") String userId,
                                   @RequestParam("userPhone") String userPhone,
                                   @RequestBody ProfileModifyRequest profileModifyRequest
    ) {

        String password = profileModifyRequest.getUserPw();

        String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";

        Pattern passwordPt = Pattern.compile(passwordPattern);

        if (!passwordPt.matcher(password).matches()) {
            System.out.println("패스워드 형식 오류");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("패스워드 형식이 맞지 않음");
        }

        Optional<Profile> result = profileRepository.findByUserIdAndUserPhoneAndUserName(userId, userPhone, userName);
        service.changePassword(result.get().getNo(), profileModifyRequest.getUserPw());
        return ResponseEntity.ok().body(result);
    }
}

