package com.example.demo.Repositroy;

import com.example.demo.Model.MyAuthKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyAuthKeyRepository extends JpaRepository<MyAuthKey, Long> {

    Optional<MyAuthKey> findAllByPhoneNumber(String phone);
    void deleteByPhoneNumber(String phone);
}
