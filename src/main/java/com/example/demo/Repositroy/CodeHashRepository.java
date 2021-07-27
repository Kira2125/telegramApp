package com.example.demo.Repositroy;

import com.example.demo.Model.CodeHash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeHashRepository extends JpaRepository<CodeHash, Long> {
    CodeHash findCodeHashByPhone(String phone);
}
