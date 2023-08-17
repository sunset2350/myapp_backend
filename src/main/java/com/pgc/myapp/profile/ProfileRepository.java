package com.pgc.myapp.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByUserId(String userId);
    Optional<Profile> findByNo(long no);



}
