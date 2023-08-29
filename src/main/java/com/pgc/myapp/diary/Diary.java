package com.pgc.myapp.diary;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@IdClass(DiaryId.class)
public class Diary {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long ownerNo;

    @Id
    private String userId;

    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @Column(nullable = false)
    private long createTime;


}