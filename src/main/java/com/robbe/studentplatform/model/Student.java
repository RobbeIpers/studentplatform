package com.robbe.studentplatform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password is mandatory")
    private String password;

    private int age;

    @Setter(value = AccessLevel.NONE)
    @Column(unique = true)
    private String username;

    @JsonIgnore
    @JoinTable(name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "course_id", referencedColumnName = "id"))
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) // eager because not able to load authenticating
    private List<Course> courses;
    public Student(String firstname, String lastname, String password, int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = password;
        this.age = age;
        this.courses = new ArrayList<>();
    }

    public Student() {
        age = 0;
    }

    public void setUsername() {
        this.username = firstname +"_"+lastname + (Calendar.getInstance().get(Calendar.YEAR) - age);
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
