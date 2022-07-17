package com.robbe.studentplatform.controller;

import com.robbe.studentplatform.exception.MissingDataException;
import com.robbe.studentplatform.model.Course;
import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.CourseRepository;
import com.robbe.studentplatform.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    CourseRepository courseRepository;
    @Autowired
    StudentRepository studentRepository;

    /**
     *  Get all courses
     * @return all courses
     */
    @GetMapping
    public Iterable<Course> getAllCourses(){
        return courseRepository.findAll();
    }
    /**
     *  Get all info on a course
     *
     * @param id the id of the course
     * @return the course with given id
     * @throws MissingDataException when no course present with that id
     */
    @GetMapping("/{id}")
    public EntityModel<Course> getCourse(@PathVariable("id") Integer id) {
        Course c = courseRepository.findById(id).orElseThrow(() -> new MissingDataException("No Course with that id"));
        return EntityModel.of(c,
                linkTo(methodOn(CourseController.class).getCourse(id)).withSelfRel(),
                linkTo(methodOn(CourseController.class).getCourseStudentsById(id)).withRel("students"),
                linkTo(methodOn(CourseController.class).getAllCourses()).withRel("courses"));
    }

    /**
     * Get all students for the course with the given id
     *
     * @param id id of the course
     * @return list of all students that attend this course
     * @throws MissingDataException when no course present with that id
     */
    @GetMapping("/{id}/students")
    public Iterable<Student> getCourseStudentsById(@PathVariable("id") Integer id){
        Optional<Course> c =courseRepository.findById(id);
        return c.<Iterable<Student>>map(Course::getStudents).orElseThrow(() -> new MissingDataException("No Course with that id"));
    }

    /**
     * Make a course
     *
     * @param c a valid course including all necessary params
     * @return the created course
     */
    @PostMapping("")
    public Course makeCourse(@RequestBody @Valid Course c){
       return courseRepository.save(c);
    }

    /**
     * Add a student to a course
     *
     * @param courseiId course the students wants to attend
     * @param studentId id of student that will be added to course
     * @return course after update
     */
    @PutMapping("/{courseId}/students/{studentId}")
    public EntityModel<Course> addStudent(@PathVariable("courseId") Integer courseiId, @PathVariable("studentId") Integer studentId){
        Course c = courseRepository.findById(courseiId).orElseThrow(() -> new MissingDataException("No Course with that id"));
        Student s = studentRepository.findById(studentId).orElseThrow(() -> new MissingDataException("No Student whith that id"));
        c.addStudent(s);
        courseRepository.save(c);
        return EntityModel.of(c,
                linkTo(methodOn(CourseController.class).getCourse(c.getId())).withSelfRel(),
                linkTo(methodOn(CourseController.class).getCourseStudentsById(c.getId())).withRel("students"),
                linkTo(methodOn(CourseController.class).getAllCourses()).withRel("courses"));

   }
   /**
    * Delete course
    *
    * @param id the course id
    * @return Course deleted
    */
    @DeleteMapping("/{id}")
    public String deleteCourseById(@PathVariable("id") Integer id) {
        if (courseRepository.findById(id).isEmpty()) {
            throw new MissingDataException("No course with that id");
        }
        courseRepository.deleteById(id);
        return "Course deleted";
    }
}
