package com.semester3.employee_service.dto;

public class EmployeeDTO {

    private Long id;
    private String name;
    private String position;
    private String department;
    private String salary;

    // No-args constructor
    public EmployeeDTO() {
    }

    // All-args constructor
    public EmployeeDTO(Long id, String name, String position, String department, String salary) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.department = department;
        this.salary = salary;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
