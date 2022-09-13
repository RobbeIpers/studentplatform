package com.robbe.studentplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.robbe.studentplatform.exception.MissingDataException;
import com.robbe.studentplatform.model.Course;
import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.CourseRepository;
import com.robbe.studentplatform.repository.StudentRepository;
import com.robbe.studentplatform.security.JwtTokenUtil;
import com.robbe.studentplatform.security.JwtUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Time;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(value = CourseController.class,
        includeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtTokenUtil.class)})
class CourseControllerTest {
    @MockBean
    CourseRepository courseRepository;
    @MockBean
    StudentRepository studentRepository;
    @Autowired
    JwtTokenUtil jwtTokenUtil;
    Course COURSE_1 = new Course(25, "TestTeacher", "testsubject", new ArrayList<>(), new Time(6), new Time(20));
    Course COURSE_2 = new Course(30, "TestTeacher2", "testsubject2", new ArrayList<>(), new Time(6), new Time(20));
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    JwtUserDetailsService jwtUserDetailsService;

    UserDetails DUMMY = new User("Robbe_Ipers1998", "password", new ArrayList<>());
    String jwtToken;
    Student STUDENT_1 = new Student("testfirst", "testlast","password",24);
    @BeforeEach
    void setAuthorization(){
        STUDENT_1.setUsername();
        jwtToken = jwtTokenUtil.generateToken(DUMMY.getUsername());
        when(studentRepository.findByUsername(DUMMY.getUsername())).thenReturn(Collections.singletonList(STUDENT_1));
        when(jwtUserDetailsService.loadUserByUsername(DUMMY.getUsername())).thenReturn(DUMMY);
    }
    @Test
    void getAllCourses() throws Exception {
        List<Course> courses = new ArrayList<>(Arrays.asList(COURSE_1, COURSE_2));
        //mockAuth();
        when(courseRepository.findAll()).thenReturn(courses);
        mvc.perform(MockMvcRequestBuilders
                .get("/courses")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[1].name", is("testsubject2")))
                .andExpect(jsonPath("$[0].name", is("testsubject")));
    }

    @Test
    void getCourse() throws Exception {
        when(courseRepository.findById(1)).thenReturn(Optional.of(COURSE_1));

        mvc.perform(MockMvcRequestBuilders
                .get("/courses/1")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(jsonPath("$.name", is("testsubject")))
                .andExpect(jsonPath("$.capacity", is(25)));
    }

    @Test
    void makeCourse() throws Exception {
    Course c = Course.builder()
                .name("Chemistry")
                .teacher("Robbe Ipers").build();

        when(courseRepository .save(c)).thenReturn(c);
        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.post("/courses")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(this.mapper.writeValueAsString(c));

        mvc.perform(mockRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$.capacity", is(25)))
            .andExpect(jsonPath("$.name", is("Chemistry")))
            .andExpect(jsonPath("$.teacher", is("Robbe Ipers")));
    }

    @Test
    void addStudent() throws Exception {
        Student s = Student.builder()
                .password("$2a$12$aFJPtJjYUNVjxW7NFwt/gORIMicd1vkLEEN1ciutRbVmcU1cNzJEi")
                .firstname("Robbe")
                .lastname("Ipers")
                .username("Robbe_Ipers1998")
                .courses(new ArrayList<>())
                .age(24).build();

        when(studentRepository.findById(1)).thenReturn(Optional.of(s));
        when(courseRepository.findById(1)).thenReturn(Optional.of(COURSE_1));
        when(courseRepository.save(COURSE_1)).thenReturn(COURSE_1);

        MockHttpServletRequestBuilder mockRequest = MockMvcRequestBuilders.put("/courses/1/students/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + jwtToken)
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(mockRequest)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", notNullValue()))
            .andExpect(jsonPath("$.students[0].firstname", is("Robbe")))
            .andExpect(jsonPath("$.students[0].lastname", is("Ipers")))
            .andExpect(jsonPath("$.name", is("testsubject")));
    }

    @Test
    public void deleteCourse_success() throws Exception {
        when(courseRepository.findById(2)).thenReturn(Optional.of(COURSE_2));

        mvc.perform(MockMvcRequestBuilders
                .delete("/courses/2")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteCourse_notFound() throws Exception {
        when(courseRepository.findById(99)).thenReturn(Optional.empty());

        mvc.perform(MockMvcRequestBuilders
                .delete("/courses/99")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result ->
                        assertTrue(result.getResolvedException() instanceof MissingDataException))
                .andExpect(result ->
                        assertEquals("No course with that id", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }
}