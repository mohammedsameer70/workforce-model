package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.config.TestSecurityConfig;
import com.boostphysioclinic.workforceapplication.dto.entity.Employee;
import com.boostphysioclinic.workforceapplication.dto.entity.EmployeeAttendance;
import com.boostphysioclinic.workforceapplication.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmployeeService employeeService;

    private Employee testEmployee;
    private EmployeeAttendance testAttendance;

    @BeforeEach
    void setUp() {
        testEmployee = createMockEmployee();
        testAttendance = createMockAttendance();
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAllEmployees() throws Exception {
        List<Employee> employees = List.of(testEmployee);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetEmployeesByDepartment() throws Exception {
        List<Employee> employees = List.of(testEmployee);
        when(employeeService.getEmployeesByDepartment("IT")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/department/IT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetEmployeesByStatus() throws Exception {
        List<Employee> employees = List.of(testEmployee);
        when(employeeService.getEmployeesByStatus("Active")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/status/Active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetEmployeesByShift() throws Exception {
        List<Employee> employees = List.of(testEmployee);
        when(employeeService.getEmployeesByShift("Morning")).thenReturn(employees);

        mockMvc.perform(get("/api/employees/shift/Morning"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetEmployeeByEmailFound() throws Exception {
        when(employeeService.getEmployeeByEmail("test@example.com")).thenReturn(Optional.of(testEmployee));

        mockMvc.perform(get("/api/employees/email/test@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetEmployeeByEmailNotFound() throws Exception {
        when(employeeService.getEmployeeByEmail("notfound@example.com")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/email/notfound@example.com"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetEmployeeByIdFound() throws Exception {
        when(employeeService.getEmployeeById(1L)).thenReturn(Optional.of(testEmployee));

        mockMvc.perform(get("/api/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetEmployeeByIdNotFound() throws Exception {
        when(employeeService.getEmployeeById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateEmployee() throws Exception {
        when(employeeService.createEmployee(any(Employee.class))).thenReturn(testEmployee);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testUpdateEmployee() throws Exception {
        when(employeeService.updateEmployee(anyLong(), any(Employee.class))).thenReturn(testEmployee);

        mockMvc.perform(put("/api/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetEmployeeAttendance() throws Exception {
        List<EmployeeAttendance> attendance = List.of(testAttendance);
        when(employeeService.getEmployeeAttendance(1L)).thenReturn(attendance);

        mockMvc.perform(get("/api/employees/1/attendance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testGetAttendanceByStatus() throws Exception {
        List<EmployeeAttendance> attendance = List.of(testAttendance);
        when(employeeService.getAttendanceByStatus("Present")).thenReturn(attendance);

        mockMvc.perform(get("/api/employees/attendance/status/Present"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testCreateEmployeeAttendance() throws Exception {
        when(employeeService.createEmployeeAttendance(any(EmployeeAttendance.class))).thenReturn(testAttendance);

        mockMvc.perform(post("/api/employees/attendance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAttendance)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testUpdateEmployeeAttendance() throws Exception {
        when(employeeService.updateEmployeeAttendance(anyLong(), any(EmployeeAttendance.class)))
                .thenReturn(testAttendance);

        mockMvc.perform(put("/api/employees/attendance/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAttendance)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = { "ADMIN" })
    void testDeleteEmployeeAttendance() throws Exception {
        mockMvc.perform(delete("/api/employees/attendance/1"))
                .andExpect(status().isNoContent());
    }

    private Employee createMockEmployee() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setEmployeeId("EMP001");
        employee.setName("John Doe");
        employee.setEmail("test@example.com");
        employee.setDepartment("IT");
        employee.setStatus("Active");
        employee.setShift("Morning");
        return employee;
    }

    private EmployeeAttendance createMockAttendance() {
        EmployeeAttendance attendance = new EmployeeAttendance();
        attendance.setId(1L);
        attendance.setEmployee(testEmployee);
        attendance.setDate(LocalDate.of(2024, 1, 1));
        attendance.setStatus("Present");
        attendance.setCheckInTime("09:00");
        attendance.setCheckOutTime("17:00");
        return attendance;
    }
}
