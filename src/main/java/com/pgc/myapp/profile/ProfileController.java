package com.pgc.myapp.profile;


import com.pgc.myapp.auth.Auth;
import com.pgc.myapp.auth.AuthProfile;
import com.pgc.myapp.auth.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/profiles")
public class ProfileController {
    Map<String, Profile> list = new ConcurrentHashMap<>();
    AtomicInteger no = new AtomicInteger(0);

    @Autowired
    ProfileRepository repository;

    @Autowired
    private AuthService service;


    @GetMapping(value = "/{userId}")
    public Optional<Profile> getProfile(@PathVariable("userId") String userId) {
        Optional<Profile> list = repository.findByLogin_UserId(userId);


        return list;
    }




    @Auth
    @GetMapping(value = "/userprofile")
    public Optional<Profile> findById(
            @RequestAttribute AuthProfile authProfile) {

        return repository.findByLogin_UserId(authProfile.getUserId());
    }


    @DeleteMapping(value = "/{no}")
    public ResponseEntity DeleteProfile(@PathVariable("no") long no) {
        repository.deleteById(no);
        return ResponseEntity.ok().build();
    }

    @Auth
    @PutMapping(value = "/userprofile")
    public ResponseEntity modifyProfile(
            @RequestAttribute AuthProfile authProfile,
            @RequestBody ProfileModifyRequest profileModifyRequest
    ) {
        Optional<Profile> findProfile = repository.findByLogin_UserId(authProfile.getUserId());



        if (findProfile.isPresent()) {
            Profile toModifyProfile = findProfile.get();

            if (profileModifyRequest.getUserName() != null && !profileModifyRequest.getUserName().isBlank()) {
                toModifyProfile.setUserName(profileModifyRequest.getUserName());
            }

            if (profileModifyRequest.getUserPhone() != null && !profileModifyRequest.getUserPhone().isBlank()) {
                toModifyProfile.setUserPhone(profileModifyRequest.getUserPhone());
            }

            if (profileModifyRequest.getUserPw() != null && !profileModifyRequest.getUserPw().isBlank()) {
                String password = profileModifyRequest.getUserPw();
                String passwordPattern = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
                Pattern passwordPt = Pattern.compile(passwordPattern);

                if (!passwordPt.matcher(password).matches()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("패스워드 형식이 맞지 않습니다.");
                }

                System.out.println(password);
                Optional<Profile> result = repository.findByUserIdAndUserPhoneAndUserName(
                        toModifyProfile.getUserId(),
                        toModifyProfile.getUserPhone(),
                        toModifyProfile.getUserName());
                service.changePassword(result.get().getNo(), profileModifyRequest.getUserPw());
            }

            repository.save(toModifyProfile);


            Map<String, Object> response = new HashMap<>();
            response.put("data", toModifyProfile);
            response.put("message", "프로필 정보가 수정되었습니다.");

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("프로필을 찾을 수 없습니다.");
    }
}
