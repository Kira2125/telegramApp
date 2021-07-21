package com.example.demo.Repositroy;

import com.example.demo.Model.MySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MySessionRepository extends JpaRepository<MySession, Long> {
    Optional<MySession> findAllByPhoneNumber(String phone);
}
