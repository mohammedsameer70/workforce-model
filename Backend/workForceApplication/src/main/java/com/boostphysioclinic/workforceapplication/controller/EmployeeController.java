package com.boostphysioclinic.workforceapplication.controller;

import com.boostphysioclinic.workforceapplication.dto.entity.Employee;
import com.boostphysioclinic.workforceapplication.dto.entity.EmployeeAttendance;
import com.boostphysioclinic.workforceapplication.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/department/{department}")
    public List<Employee> getEmployeesByDepartment(@PathVariable String department) {
        return employeeService.getEmployeesByDepartment(department);
    }

    @GetMapping("/status/{status}")
    public List<Employee> getEmployeesByStatus(@PathVariable String status) {
        return employeeService.getEmployeesByStatus(status);
    }

    @GetMapping("/shift/{shift}")
    public List<Employee> getEmployeesByShift(@PathVariable String shift) {
        return employeeService.getEmployeesByShift(shift);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Employee> getEmployeeByEmail(@PathVariable String email) {
        return employeeService.getEmployeeByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @PutMapping("/{id}")
    public Employee updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        return employeeService.updateEmployee(id, employee);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{employeeId}/attendance")
    public List<EmployeeAttendance> getEmployeeAttendance(@PathVariable Long employeeId) {
        return employeeService.getEmployeeAttendance(employeeId);
    }

    @GetMapping("/attendance/status/{status}")
    public List<EmployeeAttendance> getAttendanceByStatus(@PathVariable String status) {
        return employeeService.getAttendanceByStatus(status);
    }

    @PostMapping("/attendance")
    public EmployeeAttendance createEmployeeAttendance(@RequestBody EmployeeAttendance attendance) {
        return employeeService.createEmployeeAttendance(attendance);
    }

    @PutMapping("/attendance/{id}")
    public EmployeeAttendance updateEmployeeAttendance(@PathVariable Long id, @RequestBody EmployeeAttendance attendance) {
        return employeeService.updateEmployeeAttendance(id, attendance);
    }

    @DeleteMapping("/attendance/{id}")
    public ResponseEntity<Void> deleteEmployeeAttendance(@PathVariable Long id) {
        employeeService.deleteEmployeeAttendance(id);
        return ResponseEntity.noContent().build();
    }
}
