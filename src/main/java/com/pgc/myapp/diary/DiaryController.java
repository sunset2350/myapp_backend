package com.pgc.myapp.diary;

import com.pgc.myapp.auth.Auth;
import com.pgc.myapp.auth.AuthProfile;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping(value = "/diarys")
@Tag(name="다이어리 관리 API")
public class DiaryController {
    Map<String, Diary> list = new HashMap<>();
    AtomicLong num = new AtomicLong(0);

    @Autowired
    DiaryRepository repository;

//
//    @GetMapping
//    public List<Diary> getDiaryList() {
//        List<Diary> list = repository.findAllByOrderByOwnerNo();
//
//        return list;
//    }

    @Auth
    @PostMapping
    @Operation(summary = "다이어리 작성")
    public ResponseEntity<Map<String, Object>> addContent(@RequestBody Diary diary,
                                                          @RequestAttribute AuthProfile authProfile) {


        long no = num.incrementAndGet();
        if (diary.getTitle() == null || diary.getTitle().isEmpty()) {
            Map<String, Object> list = new HashMap<>();
            list.put("data", null);
            list.put("message", "No title");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(list);
        }
        if (diary.getContent() == null || diary.getContent().isEmpty()) {
            Map<String, Object> list = new HashMap<>();
            list.put("data", null);
            list.put("message", "No content");
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(list);
        }

        diary.setCreateTime(new Date().getTime());


        diary.setOwnerNo(no);
        diary.setUserId(authProfile.getUserId());



        Diary saveDiary = repository.save(diary);




        if (saveDiary != null) {
            Map<String, Object> list = new HashMap<>();
            list.put("data", saveDiary);
            list.put("message", "create");
            return ResponseEntity.status(HttpStatus.CREATED).body(list);
        }
        return ResponseEntity.ok().build();
    }

    @Auth
    @DeleteMapping(value = "/{ownerNo}")
    @Operation(summary = "다이어리 삭제")
    public ResponseEntity deleteContent(@PathVariable("ownerNo") Long ownerNo,
                                        @RequestAttribute AuthProfile authProfile) {
        Optional<Diary> diary = repository.findById(new
                DiaryId(ownerNo, authProfile.getUserId()));

        repository.deleteById(new DiaryId(ownerNo, authProfile.getUserId()));
        return ResponseEntity.ok().build();
    }


    @Auth
    @PutMapping(value = "/{ownerNo}")
    @Operation(summary = "다이어리 수정")
    public ResponseEntity modifyContent(@PathVariable long ownerNo, @RequestBody DiaryModifyRequest diary,
                                        @RequestAttribute AuthProfile authProfile) {
        Optional<Diary> searchDiary = repository.findById(new DiaryId(ownerNo, authProfile.getUserId()));

        if (!searchDiary.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Diary toModifyDiary = searchDiary.get();
        if (diary != null) {
            if (diary.getTitle() != null && !diary.getTitle().isBlank()) {
                toModifyDiary.setTitle(diary.getTitle());
            }
            if (diary.getContent() != null && !diary.getContent().isBlank()) {
                toModifyDiary.setContent(diary.getContent());
            }
            repository.save(toModifyDiary);

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

//    @Operation(summary = "다이어리 조회", security = { @SecurityRequirement(name = "bearer-key") })
    @Auth
    @GetMapping(value = "/paging")
    @Operation(summary = "다이어리 보기")
    public Page<Diary> getDiaryPaging(@RequestParam int page, @RequestParam int size,
                                      @RequestAttribute AuthProfile authProfile) {
        Sort sort = Sort.by("ownerNo").descending();

        PageRequest pageRequest = PageRequest.of(page, size, sort);

        return repository.findByUserId(authProfile.getUserId(), pageRequest);
    }

    @Auth
    @GetMapping(value = "/paging/searchByTitle")
    @Operation(summary = "다이어리 제목 검색")
    public Page<Diary> getDiarySearchByTitle(@RequestParam int page, @RequestParam int size, @RequestParam String title,
                                      @RequestAttribute AuthProfile authProfile) {
        Sort sort = Sort.by("ownerNo").descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return repository.findByUserIdAndTitleContains(authProfile.getUserId(), title, pageRequest);
    }

    @Auth
    @GetMapping(value = "/paging/searchByContent")
    @Operation(summary = "다이어리 본문 검색")
    public Page<Diary> getDiarySearchByContent(@RequestParam int page, @RequestParam int size, @RequestParam String content,
                                       @RequestAttribute AuthProfile authProfile) {
        Sort sort = Sort.by("ownerNo").descending();
        PageRequest pageRequest = PageRequest.of(page, size, sort);
        return repository.findByUserIdAndContentContains(authProfile.getUserId(), content, pageRequest);
    }

    @Auth
    @GetMapping(value = "/paging/ownerNo")
    @Operation(summary = "다이어리 상세 보기")
    public Optional<Diary> getDiaryDetails(@RequestParam long ownerNo, @RequestAttribute AuthProfile authProfile) {
        System.out.println(authProfile.getUserId());
        return repository.findByUserIdAndOwnerNo(authProfile.getUserId(), ownerNo);
    }


}