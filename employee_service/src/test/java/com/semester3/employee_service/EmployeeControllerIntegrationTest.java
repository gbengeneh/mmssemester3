package com.semester3.employee_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.semester3.employee_service.dto.EmployeeDTO;
import com.semester3.employee_service.entity.Employee;
import com.semester3.employee_service.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class EmployeeControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployeeRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee employee;
    private EmployeeDTO dto;

    @BeforeEach
    void setUp() {
        repository.deleteAll(); // Clean up
        employee = new Employee(null, "John Doe", "Developer", "IT", "50000");
        Employee saved = repository.save(employee);
        dto = new EmployeeDTO(saved.getId(), "John Doe", "Developer", "IT", "50000");
    }

    @Test
    void testGetAllEmployees() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].name", is("John Doe")));
    }

    @Test
    void testGetEmployeeById_Success() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", dto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.name", is("John Doe")));
    }

    @Test
    void testGetEmployeeById_NotFound() throws Exception {
        mockMvc.perform(get("/api/employees/{id}", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee Not Found")));
    }

    @Test
    void testCreateEmployee() throws Exception {
        EmployeeDTO newDto = new EmployeeDTO(null, "Jane Doe", "Manager", "HR", "60000");
        String json = objectMapper.writeValueAsString(newDto);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Jane Doe")))
                .andExpect(jsonPath("$.position", is("Manager")));
    }

    @Test
    void testCreateEmployee_ValidationError() throws Exception {
        EmployeeDTO invalidDto = new EmployeeDTO(null, "", "Manager", "HR", "60000"); // Empty name
        String json = objectMapper.writeValueAsString(invalidDto);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateEmployee_Success() throws Exception {
        EmployeeDTO updateDto = new EmployeeDTO(null, "Updated Name", "Updated Position", "Updated Dept", "70000");
        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/employees/{id}", dto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Name")))
                .andExpect(jsonPath("$.position", is("Updated Position")));
    }

    @Test
    void testUpdateEmployee_NotFound() throws Exception {
        EmployeeDTO updateDto = new EmployeeDTO(null, "Updated Name", "Updated Position", "Updated Dept", "70000");
        String json = objectMapper.writeValueAsString(updateDto);

        mockMvc.perform(put("/api/employees/{id}", 999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("Employee not found")));
    }

    @Test
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/employees/{id}", dto.getId()))
                .andExpect(status().isNoContent());

        // Verify deleted
        mockMvc.perform(get("/api/employees/{id}", dto.getId()))
                .andExpect(status().isNotFound());
    }
}
