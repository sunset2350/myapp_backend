package com.pgc.myapp.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthProfile {
    private long no;
    private String userName;
    private String userId;
    private String userPhone;
    private String userBirth;
}
