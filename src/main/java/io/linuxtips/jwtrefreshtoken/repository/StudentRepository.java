package io.linuxtips.jwtrefreshtoken.repository;

import io.linuxtips.jwtrefreshtoken.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student,Long> {


}
