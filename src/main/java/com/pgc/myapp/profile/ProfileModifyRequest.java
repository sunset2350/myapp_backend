package com.pgc.myapp.profile;

import com.pgc.myapp.auth.entity.Login;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileModifyRequest {
    @Column(nullable = false)
    private String userName;
    @Column(nullable = false)
    private String userPhone;
    private String userBirth;

    @Column(nullable = false)
    private String userPw;

}
