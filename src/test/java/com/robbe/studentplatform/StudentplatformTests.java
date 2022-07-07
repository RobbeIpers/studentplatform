package com.robbe.studentplatform;

import com.robbe.studentplatform.model.Student;
import com.robbe.studentplatform.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.text.ParseException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class StudentplatformTests {
    @MockBean
    StudentRepository studentRepository;
    @Test
    void contextLoads() {
    }
    @Test
    public void testStudentSave() {
        Student student = new Student("Robbe", "Ipers", 24);
        studentRepository.save(student);
        assertNotNull(student.getId());
    }

}
