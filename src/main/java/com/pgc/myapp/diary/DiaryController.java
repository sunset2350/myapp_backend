package com.pgc.myapp.diary;

import com.pgc.myapp.auth.AuthProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping(value = "/diarys")
public class DiaryController {
    AtomicInteger no = new AtomicInteger(0);
    Map<String, Diary> list = new HashMap<>();


    @Autowired
    DiaryRepository repository;


    @GetMapping
    public List<Diary> getDiaryList() {
        List<Diary> list = repository.findAllByOrderByOwnerNo();

        return list;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addContent(@RequestBody Diary diary,
                                                          @RequestAttribute AuthProfile authProfile) {
        if (diary.getUserPw() == null || diary.getUserPw().isEmpty()) {
            Map<String, Object> list = new HashMap<>();
            list.put("data", null);
            list.put("message", "No Pw");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(list);
        }
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


        repository.save(diary);
        diary.setOwnerNo(authProfile.getNo());
        Diary saveDiary = repository.save(diary);


        if(saveDiary != null){
            Map<String , Object> list = new HashMap<>();
            list.put("data", diary);
            list.put("message", "create");
            return ResponseEntity.status(HttpStatus.CREATED).body(list);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{no}")
    public ResponseEntity deleteContent(@PathVariable("no") Long no,
                                        @RequestAttribute AuthProfile authProfile)
    {
        Optional<Diary> diary = repository.findById(new
                DiaryId(authProfile.getNo(), authProfile.getUserId()));

        repository.deleteById(new DiaryId(authProfile.getNo(), authProfile.getUserId()));
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{no}")
    public ResponseEntity modifyContent(@PathVariable long no, @RequestBody Diary diary,
                                        @RequestAttribute AuthProfile authProfile) {
        Optional<Diary> searchDiary = repository.findById(new DiaryId(authProfile.getNo(), authProfile.getUserId()));

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
            if (diary.getUserPw() != null && !diary.getUserPw().isBlank()) {
                toModifyDiary.setUserPw(diary.getUserPw());
            }
            repository.save(toModifyDiary);
            Map<String, Object> list = new HashMap<>();
            list.put("data", toModifyDiary);
            list.put("message", "게시글 수정 완료");

            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    @GetMapping(value = "/paging")
    public Page<Diary> getDiaryPaging(@RequestParam int page, @RequestParam int size,
                                      @RequestAttribute AuthProfile authProfile){
        Sort sort = Sort.by("no").descending();

        PageRequest pageRequest = PageRequest.of(page,size,sort);
        return repository.findByOwnerNo(authProfile.getNo(),pageRequest);
    }

    @GetMapping(value = "/paging/searchByTitle")
    public Page<Diary> getDiarySearch(@RequestParam int page, @RequestParam int size, @RequestParam String title,
                                      @RequestAttribute AuthProfile authProfile){
        Sort sort = Sort.by("no").descending();
        PageRequest pageRequest = PageRequest.of(page, size,sort);
        return repository.findByTitleContains(title,pageRequest);
    }

    @GetMapping(value = "/paging/searchByContent")
    public Page<Diary> getDiaryContent(@RequestParam int page, @RequestParam int size, @RequestParam String content){
        Sort sort = Sort.by("no").descending();
        PageRequest pageRequest = PageRequest.of(page,size,sort);
        return repository.findByContentContains(content,pageRequest);
    }

    @GetMapping(value = "/paging/no")
    public Optional<Diary> getDiaryDetails(@RequestParam long no){
        return repository.findByOwnerNo(no);
    }
}