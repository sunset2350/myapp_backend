package com.pgc.myapp.diary;


import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping(value = "/profile")
public class ProfileController {
    List<Profile> list = new ArrayList<>();
    AtomicInteger num = new AtomicInteger(0);

    @GetMapping
    public List<Profile> getProfileList() {
        return list;
    }

    @PostMapping
    public List<Profile> addProfile(@RequestBody Profile profile){
        int no = num.incrementAndGet();
        System.out.println(no);
        list.add(profile);



        return list;
    }
}
