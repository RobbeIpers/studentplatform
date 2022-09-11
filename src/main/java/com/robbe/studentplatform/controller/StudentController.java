package com.robbe.studentplatform.controller;

import com.robbe.studentplatform.exception.MissingDataException;
import com.robbe.studentplatform.model.Course;
import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Get a student
     *
     * @param id the id of the wanted student
     * @return student
     */
    @GetMapping("/{id}")
    public EntityModel<Student> getStudent(@PathVariable("id") Integer id) {
        Student s = studentRepository.findById(id).orElseThrow(() -> new MissingDataException("No student whit that id"));
        return EntityModel.of(s, //
                linkTo(methodOn(StudentController.class).getStudent(id)).withSelfRel(),
                linkTo(methodOn(StudentController.class).getStudentCoursesById(id)).withRel("My courses"),
                linkTo(methodOn(StudentController.class).getAllStudents()).withRel("Students"));
    }

    /**
     * Get all courses of a student
     *
     * @param id id of student
     * @return list of courses
     */
    @GetMapping("/{id}/courses")
    public List<Course> getStudentCoursesById(@PathVariable("id") Integer id) {
        Student s = studentRepository.findById(id).orElseThrow(() -> new MissingDataException("No student whit that id"));
        return s.getCourses();
    }

    /**
     * get all students
     *
     * @return a list of all students
     */
    @GetMapping
    public Iterable<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    /** Temporary method for development to delete all students in db
     *
     * @return All students deleted
     */
    @DeleteMapping("")
    public String deleteStudents(){
        studentRepository.deleteAll();
        return "All students deleted";
    }

    /**
     * Delete student
     *
     * @param id id of student to be deleted
     * @return Student deleted
     */
    @DeleteMapping("/{id}")
    public String deleteStudentById(@PathVariable("id") Integer id){
        if (studentRepository.findById(id).isEmpty()) {
            throw new MissingDataException("No student with that id");
        }
        studentRepository.deleteById(id);
        return "Student deleted";
    }

    /**
     * Create a new student
     *
     * @param s student with all the needed params
     * @return created student
     */
    @PostMapping("")
    public Student makeStudent(@RequestBody @Valid Student s){
        s.setUsername();
        s.setPassword(passwordEncoder.encode(s.getPassword()));
        return studentRepository.save(s);
    }
}
