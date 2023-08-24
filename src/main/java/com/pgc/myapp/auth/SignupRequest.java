package com.pgc.myapp.auth;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupRequest {
    private String userId;
    private String userPw;
    private String userName;
    private String userPhone;
    private String userBirth;
}
