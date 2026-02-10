package com.semester3.employee_service;

import com.semester3.employee_service.entity.Employee;
import com.semester3.employee_service.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository repository;

    @Test
    void testSaveAndFindById() {
        // Given
        Employee employee = new Employee(null, "John Doe", "Developer", "IT", "50000");

        // When
        Employee saved = repository.save(employee);

        // Then
        assertNotNull(saved.getId());
        Optional<Employee> found = repository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("John Doe", found.get().getName());
    }

    @Test
    void testFindAll() {
        // Given
        Employee emp1 = new Employee(null, "John Doe", "Developer", "IT", "50000");
        Employee emp2 = new Employee(null, "Jane Doe", "Manager", "HR", "60000");
        repository.save(emp1);
        repository.save(emp2);

        // When
        List<Employee> employees = repository.findAll();

        // Then
        assertEquals(2, employees.size());
    }

    @Test
    void testDeleteById() {
        // Given
        Employee employee = new Employee(null, "John Doe", "Developer", "IT", "50000");
        Employee saved = repository.save(employee);

        // When
        repository.deleteById(saved.getId());

        // Then
        Optional<Employee> found = repository.findById(saved.getId());
        assertFalse(found.isPresent());
    }

    @Test
    void testFindById_NotFound() {
        // When
        Optional<Employee> found = repository.findById(999L);

        // Then
        assertFalse(found.isPresent());
    }
}
