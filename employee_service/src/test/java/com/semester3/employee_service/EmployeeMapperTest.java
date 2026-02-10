package com.semester3.employee_service;

import com.semester3.employee_service.dto.EmployeeDTO;
import com.semester3.employee_service.entity.Employee;
import com.semester3.employee_service.utility.EmployeeMapper;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EmployeeMapperTest {

    @Test
    void testToDTO() {
        // Given
        Employee employee = new Employee(1L, "John Doe", "Developer", "IT", "50000");

        // When
        EmployeeDTO dto = EmployeeMapper.toDTO(employee);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("John Doe", dto.getName());
        assertEquals("Developer", dto.getPosition());
        assertEquals("IT", dto.getDepartment());
        assertEquals("50000", dto.getSalary());
    }

    @Test
    void testToEntity() {
        // Given
        EmployeeDTO dto = new EmployeeDTO(1L, "Jane Doe", "Manager", "HR", "60000");

        // When
        Employee employee = EmployeeMapper.toEntity(dto);

        // Then
        assertNotNull(employee);
        assertEquals(1L, employee.getId());
        assertEquals("Jane Doe", employee.getName());
        assertEquals("Manager", employee.getPosition());
        assertEquals("HR", employee.getDepartment());
        assertEquals("60000", employee.getSalary());
    }

    @Test
    void testToDTOWithNullValues() {
        // Given
        Employee employee = new Employee(null, null, null, null, null);

        // When
        EmployeeDTO dto = EmployeeMapper.toDTO(employee);

        // Then
        assertNotNull(dto);
        assertNull(dto.getId());
        assertNull(dto.getName());
        assertNull(dto.getPosition());
        assertNull(dto.getDepartment());
        assertNull(dto.getSalary());
    }

    @Test
    void testToEntityWithNullValues() {
        // Given
        EmployeeDTO dto = new EmployeeDTO(null, null, null, null, null);

        // When
        Employee employee = EmployeeMapper.toEntity(dto);

        // Then
        assertNotNull(employee);
        assertNull(employee.getId());
        assertNull(employee.getName());
        assertNull(employee.getPosition());
        assertNull(employee.getDepartment());
        assertNull(employee.getSalary());
    }
}
