package com.robbe.studentplatform.repository;

import com.robbe.studentplatform.model.Student;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends CrudRepository<Student, Integer> {
    List<Student> findByLastname(@Param("lastname") String lastname);
}
