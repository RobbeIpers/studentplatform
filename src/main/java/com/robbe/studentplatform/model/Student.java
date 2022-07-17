package com.robbe.studentplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.*;
@Data
@Builder
@AllArgsConstructor
@Entity
public class Student {
    @Id
    @Setter(value = AccessLevel.NONE)
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Integer id;
    @NotBlank(message = "Firstname is mandatory")
    private String firstname;
    @NotBlank(message = "Lastname is mandatory")
    private String lastname;
    private int age;

    @ManyToMany(cascade = CascadeType.ALL)
    @JsonIgnore
    @JoinTable(name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"))
    private List<Course> courses;
    public Student(String firstname, String lastname, int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
        this.courses = new ArrayList<>();
    }

    public Student() {
        firstname = "default";
        lastname = "default";
        age = 0;
    }

    /**
     * add course to student
     *
     * @param c course to be added
     */
    public void addCourse(Course c){
        courses.add(c);
    }
}
