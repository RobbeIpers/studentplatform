package com.robbe.studentplatform.repository;

import com.robbe.studentplatform.model.Student;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Integer> {
// this is auto implemented by spring
}
