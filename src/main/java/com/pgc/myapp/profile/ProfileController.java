package com.pgc.myapp.profile;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping(value = "/profiles")
public class ProfileController {
    Map<String,Profile> list  = new ConcurrentHashMap<>();
    AtomicInteger no = new AtomicInteger(0);
    @Autowired
    ProfileRepository repository;

    @GetMapping
    public List<Profile> getProfileList() {
        List<Profile> list = repository.findAll(Sort.by("no").ascending());
        return list;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addProfile(@RequestBody Profile profile) {

        if (profile.getUserId() == null || profile.getUserId().isEmpty()) {
            Map<String, Object> list = new HashMap<>();
            list.put("data", null);
            list.put("message", "No Id");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(list);
        }
        if (profile.getUserPw() == null || profile.getUserPw().isEmpty()) {
            Map<String, Object> list = new HashMap<>();
            list.put("data", null);
            list.put("message", "No Pw");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(list);
        }
        if (profile.getUserName() == null || profile.getUserName().isEmpty()) {
            Map<String, Object> list = new HashMap<>();
            list.put("data", null);
            list.put("message", "No Name");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(list);
        }
        if (profile.getUserPhone() == null || profile.getUserPhone().isEmpty()) {
            Map<String, Object> list = new HashMap<>();
            list.put("data", null);
            list.put("message", "No Phone");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(list);
        }
        if (profile.getUserBirth() == null || profile.getUserBirth().isEmpty()) {
            Map<String, Object> list = new HashMap<>();
            list.put("data", null);
            list.put("message", "No Birth");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(list);
        }
        Optional<Profile> checkId = repository.findByUserId(profile.getUserId());
        if(checkId.isPresent()) {
            return (ResponseEntity<Map<String, Object>>) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
        } else {
            repository.save(profile);
        }

        if(repository != null) {
            Map<String , Object> list = new HashMap<>();
            list.put("data", profile);
            list.put("message", "create");
            return ResponseEntity.status(HttpStatus.CREATED).body(list);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{no}")
    public ResponseEntity DeleteProfile(@PathVariable("no") long no) {
        repository.deleteById(no);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{no}")
    public ResponseEntity modifyProfile(@PathVariable long no, @RequestBody Profile profile) {
        Optional<Profile> findProfile = repository.findById(no);

        if (!findProfile.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Profile toModifyProfile = findProfile.get();
        if (profile != null) {
            if (profile.getUserName() != null && !profile.getUserName().isBlank()) {
                toModifyProfile.setUserName(profile.getUserName());
            }
            if (profile.getUserPhone() != null && !profile.getUserPhone().isBlank()) {
                toModifyProfile.setUserPhone(profile.getUserPhone());
            }

            repository.save(toModifyProfile);

            Map<String, Object> list = new HashMap<>();
            list.put("data", toModifyProfile);
            list.put("message", "프로필 정보가 수정되었습니다.");

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }
}
