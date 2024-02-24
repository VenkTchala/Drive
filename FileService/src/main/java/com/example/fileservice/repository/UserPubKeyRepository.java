package com.example.fileservice.repository;

import com.example.fileservice.entity.UserPubKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserPubKeyRepository extends JpaRepository<UserPubKey, Long> {
    UserPubKey getUserPubKeyByUser_Username (String username);
}
