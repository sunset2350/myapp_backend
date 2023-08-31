package com.pgc.myapp.profile;

import com.pgc.myapp.auth.entity.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByLogin_No(long no);



    Optional<Profile> findByUserNameAndUserBirth(String userName, String userBirth);

    Optional<Profile> findByUserIdAndUserPhoneAndUserName(String userId, String userPhone, String userName);

    Optional<Profile> findByLogin_UserId(String userId);

    List<Profile> findAll();

    Optional<Profile> findByNoAndUserId(long no, String userId);




}
