package com.robbe.studentplatform.controller;

import com.robbe.studentplatform.model.Course;
import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.CourseRepository;
import com.robbe.studentplatform.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired // automatically get bean called StudentRepository to handle data
    private StudentRepository studentRepository;

    @Autowired // automatically get bean called StudentRepository to handle data
    private CourseRepository courseRepository;

    @GetMapping("/{id}")
    public EntityModel<Student> getStudent(@PathVariable("id") Integer id) {
        Student s = studentRepository.findById(id).orElseThrow(); //todo exceptions
        return EntityModel.of(s, //
                linkTo(methodOn(StudentController.class).getStudent(id)).withSelfRel(),
                linkTo(methodOn(StudentController.class).getStudentCoursesById(id)).withRel("My courses"),
                linkTo(methodOn(StudentController.class).getAllStudents()).withRel("Students"));
    }
    @GetMapping("/{id}/courses")
    public List<Course> getStudentCoursesById(@PathVariable("id") Integer id) {
        Student s = studentRepository.findById(id).orElseThrow(); //todo exceptions
        return s.getCourses();
    }
    @GetMapping
    public Iterable<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    // temporary method for development to clear students
    @DeleteMapping("")
    public String deleteStudents(){
        studentRepository.deleteAll();
        return "All students deleted";
    }

    // temporary method for development to clear students
    @DeleteMapping("/{id}")
    public String deleteStudentById(@PathVariable("id") Integer id){
        studentRepository.deleteById(id);
        return "Student deleted";
    }

   // create a student
    @PostMapping("")
    public Student makeStudent(@RequestBody @Valid Student s){
        return studentRepository.save(s);
    }
}
