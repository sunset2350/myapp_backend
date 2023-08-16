package com.pgc.myapp.diary;

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
        List<Diary> list = repository.findAll(Sort.by("no").ascending());
        return list;
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addContent(@RequestBody Diary diary) {
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
        Optional<Diary> saveDiary = repository.findById(diary.getNo());


        if(saveDiary.isPresent()){
            Map<String , Object> list = new HashMap<>();
            list.put("data", diary);
            list.put("message", "create");
            return ResponseEntity.status(HttpStatus.CREATED).body(list);
        }
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/{no}")
    public ResponseEntity deleteContent(@PathVariable("no") Long no){
        repository.deleteById(no);
        return ResponseEntity.ok().build();
    }

    @PutMapping(value = "/{no}")
    public ResponseEntity modifyContent(@PathVariable long no, @RequestBody Diary diary) {
        Optional<Diary> searchDiary = repository.findById(no);

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
    public Page<Diary> getDiaryPaging(@RequestParam int page, @RequestParam int size){
        Sort sort = Sort.by("no").descending();

        PageRequest pageRequest = PageRequest.of(page,size,sort);
        return repository.findAll(pageRequest);
    }

    @GetMapping(value = "/{title}")
    public Page<Diary> getDiarySearch(@RequestParam String title){
        Sort sort = Sort.by("no").descending();

        return null;
    }


}