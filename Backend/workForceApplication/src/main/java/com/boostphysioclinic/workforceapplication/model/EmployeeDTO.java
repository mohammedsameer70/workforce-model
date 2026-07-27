package com.boostphysioclinic.workforceapplication.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class EmployeeDTO {
    @Id
    private Long transid;
    private String userName;
}
