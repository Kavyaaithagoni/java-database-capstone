package com.yourpackage.repository; // change package as needed

import com.yourpackage.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // 1. Find doctor by email
    Doctor findByEmail(String email);

    // 2. Find doctors by partial name match (custom query with LIKE)
    @Query("SELECT d FROM Doctor d " +
           "WHERE d.name LIKE CONCAT('%', :name, '%')")
    List<Doctor> findByNameLike(String name);

    // 3. Find doctors by partial name + exact specialty (case-insensitive)
    @Query("SELECT d FROM Doctor d " +
           "WHERE LOWER(d.name) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND LOWER(d.specialty) = LOWER(:specialty)")
    List<Doctor> findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(
            String name,
            String specialty
    );

    // 4. Find doctors by specialty (ignore case)
    List<Doctor> findBySpecialtyIgnoreCase(String specialty);
}