package com.pgc.myapp.diary;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DiaryId implements Serializable {
    private long ownerNo;
    private String userId;
}
