package io.linuxtips.jwtrefreshtoken.service;

import io.linuxtips.jwtrefreshtoken.model.Student;
import io.linuxtips.jwtrefreshtoken.repository.StudentRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent (Student student){
        return studentRepository.save(student);
    }

    public List<Student> listAllStudents(){
        return studentRepository.findAll();
    }

    public ResponseEntity<Student> findStudentById(Long id){
        return  studentRepository.findById(id)
                .map(student -> ResponseEntity.ok().body(student))
                .orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Student> updateStudentById(Student student, Long id){
        return studentRepository.findById(id)
                .map(studentToUpdate ->{
                    studentToUpdate.setName(student.getName());
                    studentToUpdate.setStack(student.getStack());
                    studentToUpdate.setYearsExperience(student.getYearsExperience());
                    return ResponseEntity.ok().body(student);
                }).orElse(ResponseEntity.notFound().build());
    }

    public ResponseEntity<Object> deleteStudentById (Long id){
        return studentRepository.findById(id)
                .map(studentToDelete ->{
                    studentRepository.deleteById(id);
                    return ResponseEntity.noContent().build();
                }).orElse(ResponseEntity.notFound().build());

    }
}
