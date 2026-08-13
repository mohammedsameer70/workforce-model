package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.EmployeeRepository;
import com.boostphysioclinic.workforceapplication.Repository.EmployeeAttendanceRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.Employee;
import com.boostphysioclinic.workforceapplication.dto.entity.EmployeeAttendance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeAttendanceRepository employeeAttendanceRepository;

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    public List<Employee> getEmployeesByStatus(String status) {
        return employeeRepository.findByStatus(status);
    }

    public List<Employee> getEmployeesByShift(String shift) {
        return employeeRepository.findByShift(shift);
    }

    public Optional<Employee> getEmployeeByEmail(String email) {
        return employeeRepository.findByEmail(email);
    }

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Long id, Employee employee) {
        employee.setId(id);
        return employeeRepository.save(employee);
    }

    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    public List<EmployeeAttendance> getEmployeeAttendance(Long employeeId) {
        return employeeAttendanceRepository.findByEmployeeId(employeeId);
    }

    public List<EmployeeAttendance> getAttendanceByStatus(String status) {
        return employeeAttendanceRepository.findByStatus(status);
    }

    public EmployeeAttendance createEmployeeAttendance(EmployeeAttendance attendance) {
        return employeeAttendanceRepository.save(attendance);
    }

    public EmployeeAttendance updateEmployeeAttendance(Long id, EmployeeAttendance attendance) {
        attendance.setId(id);
        return employeeAttendanceRepository.save(attendance);
    }

    public void deleteEmployeeAttendance(Long id) {
        employeeAttendanceRepository.deleteById(id);
    }
}
