package com.robbe.studentplatform.repository;

import com.robbe.studentplatform.model.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository  extends CrudRepository<Course, Integer> {
    List<Course> findByName(@Param("name") String name);
}
