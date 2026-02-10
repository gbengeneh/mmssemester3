package com.semester3.employee_service;

import com.semester3.employee_service.dto.EmployeeDTO;
import com.semester3.employee_service.entity.Employee;
import com.semester3.employee_service.exception.ResourceNotFoundException;
import com.semester3.employee_service.repository.EmployeeRepository;
import com.semester3.employee_service.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository repository;

    @InjectMocks
    private EmployeeService service;

    private Employee employee;
    private EmployeeDTO dto;

    @BeforeEach
    void setUp() {
        employee = new Employee(1L, "John Doe", "Developer", "IT", "50000");
        dto = new EmployeeDTO(1L, "John Doe", "Developer", "IT", "50000");
    }

    @Test
    void testGetAllEmployees() {
        // Given
        List<Employee> employees = Arrays.asList(employee);
        when(repository.findAll()).thenReturn(employees);

        // When
        List<EmployeeDTO> result = service.getAllEmployees();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(dto.getName(), result.get(0).getName());
        verify(repository, times(1)).findAll();
    }

    @Test
    void testGetEmployeeById_Success() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.of(employee));

        // When
        EmployeeDTO result = service.getEmployeeById(1L);

        // Then
        assertNotNull(result);
        assertEquals(dto.getId(), result.getId());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testGetEmployeeById_NotFound() {
        // Given
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.getEmployeeById(1L));
        verify(repository, times(1)).findById(1L);
    }

    @Test
    void testCreateEmployee() {
        // Given
        when(repository.save(any(Employee.class))).thenReturn(employee);

        // When
        EmployeeDTO result = service.createEmployee(dto);

        // Then
        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_Success() {
        // Given
        EmployeeDTO updateDto = new EmployeeDTO(null, "Updated Name", "Updated Position", "Updated Dept", "60000");
        Employee updatedEmployee = new Employee(1L, "Updated Name", "Updated Position", "Updated Dept", "60000");
        when(repository.findById(1L)).thenReturn(Optional.of(employee));
        when(repository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // When
        EmployeeDTO result = service.updateEmployee(1L, updateDto);

        // Then
        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(any(Employee.class));
    }

    @Test
    void testUpdateEmployee_NotFound() {
        // Given
        EmployeeDTO updateDto = new EmployeeDTO(null, "Updated Name", "Updated Position", "Updated Dept", "60000");
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.updateEmployee(1L, updateDto));
        verify(repository, times(1)).findById(1L);
        verify(repository, never()).save(any(Employee.class));
    }

    @Test
    void testDeleteEmployee() {
        // When
        service.deleteEmployee(1L);

        // Then
        verify(repository, times(1)).deleteById(1L);
    }
}
