package com.yourpackage.repository;  // change package as needed

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.yourpackage.model.Admin;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Custom query method to find admin by username
    Admin findByUsername(String username);
}