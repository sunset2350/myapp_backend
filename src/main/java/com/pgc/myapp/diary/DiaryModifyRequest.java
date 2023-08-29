package com.pgc.myapp.diary;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DiaryModifyRequest {
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
}
