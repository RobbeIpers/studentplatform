package com.robbe.studentplatform.repository;

import com.robbe.studentplatform.model.Course;
import org.springframework.data.repository.CrudRepository;

public interface CourseRepository  extends CrudRepository<Course, Integer> {

}
