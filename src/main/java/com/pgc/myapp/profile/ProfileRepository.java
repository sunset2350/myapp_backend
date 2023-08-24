package com.pgc.myapp.profile;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByNo(long no);

    Optional<Profile> findByLogin_No(long no);






}
