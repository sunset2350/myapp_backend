package com.pgc.myapp.diary;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository <Diary, DiaryId> {
    Page<Diary> findByTitleContains(String title, Pageable pageable);

    Page<Diary> findByContentContains(String content, Pageable pageable);


    Optional<Diary> findByOwnerNo(long ownerNo);

    Page<Diary> findByOwnerNo(long ownerNo, Pageable pageable);



}
