package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.Repository.AlertRepository;
import com.boostphysioclinic.workforceapplication.Repository.CapacityUtilizationRepository;
import com.boostphysioclinic.workforceapplication.Repository.EmployeeAttendanceRepository;
import com.boostphysioclinic.workforceapplication.Repository.EmployeeRepository;
import com.boostphysioclinic.workforceapplication.Repository.PerformanceMetricRepository;
import com.boostphysioclinic.workforceapplication.Repository.PredictionResultRepository;
import com.boostphysioclinic.workforceapplication.Repository.PredictionRunRepository;
import com.boostphysioclinic.workforceapplication.dto.entity.Alert;
import com.boostphysioclinic.workforceapplication.dto.entity.CapacityUtilization;
import com.boostphysioclinic.workforceapplication.dto.entity.Employee;
import com.boostphysioclinic.workforceapplication.dto.entity.EmployeeAttendance;
import com.boostphysioclinic.workforceapplication.dto.entity.PerformanceMetric;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionResult;
import com.boostphysioclinic.workforceapplication.dto.entity.PredictionRun;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CSVImportService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeAttendanceRepository employeeAttendanceRepository;
    private final AlertRepository alertRepository;
    private final CapacityUtilizationRepository capacityUtilizationRepository;
    private final PerformanceMetricRepository performanceMetricRepository;
    private final PredictionResultRepository predictionResultRepository;
    private final PredictionRunRepository predictionRunRepository;


    // ============================================================
    // DATE FORMATTERS
    // ============================================================

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");


    // ============================================================
    // EXPECTED CSV COLUMN COUNT
    // ============================================================

    private static final int EXPECTED_COLUMNS = 65;


    // ============================================================
    // CSV COLUMN INDEXES
    //
    // CLEANED CSV STRUCTURE
    // ============================================================

    private static final int COL_EMPLOYEE_ID = 0;
    private static final int COL_GENDER = 1;
    private static final int COL_AGE = 2;
    private static final int COL_DEPARTMENT = 3;
    private static final int COL_JOB_ROLE = 4;
    private static final int COL_EMPLOYMENT_TYPE = 5;
    private static final int COL_EXPERIENCE_YEARS = 6;
    private static final int COL_TEAM = 7;
    private static final int COL_BRANCH = 8;
    private static final int COL_LOCATION = 9;

    private static final int COL_ATTENDANCE_DATE = 10;

    /*
     * Columns 11 - 14 are calendar/date-related fields
     * and are not required for the employee table.
     */

    private static final int COL_SHIFT = 15;
    private static final int COL_PREFERRED_SHIFT = 16;
    private static final int COL_ATTENDANCE_STATUS = 17;

    private static final int COL_WORKING_HOURS = 18;

    /*
     * Column 19 is BreakHours.
     */
    private static final int COL_OVERTIME_HOURS = 20;

    /*
     * Columns 21 - 22 are additional attendance fields.
     */

    private static final int COL_PRODUCTIVITY = 23;
    private static final int COL_PERFORMANCE_RATING = 24;

    /*
     * Columns 25 - 26 are additional performance fields.
     */

    private static final int COL_ATTENDANCE_RATE = 27;
    private static final int COL_UTILIZATION_RATE = 28;
    private static final int COL_CAPACITY_UTILIZATION = 29;
    private static final int COL_EFFICIENCY_SCORE = 30;

    /*
     * Columns 31 - 32 are additional capacity fields.
     */

    private static final int COL_CUSTOMER_ORDERS = 33;

    /*
     * Columns 34 - 35 are operational fields.
     */

    private static final int COL_CURRENT_CAPACITY = 36;
    private static final int COL_REQUIRED_CAPACITY = 37;
    private static final int COL_AVAILABLE_HEADROOM = 38;
    private static final int COL_CAPACITY_LOAD = 39;
    private static final int COL_PEAK_UTILIZATION = 40;
    private static final int COL_SCALING_EVENTS = 41;

    /*
     * Columns 42 - 45 are additional operational/calendar fields.
     */

    private static final int COL_HISTORICAL_DEMAND = 46;
    private static final int COL_WORKFORCE_DEMAND = 47;
    private static final int COL_WORKFORCE_STATUS = 48;
    private static final int COL_ALERT_LEVEL = 49;
    private static final int COL_NOTIFICATION_TYPE = 50;
    private static final int COL_PROCESSING_STATUS = 51;

    private static final int COL_ALERT_ID = 52;
    private static final int COL_ALERT_TYPE = 53;
    private static final int COL_ALERT_SEVERITY = 54;
    private static final int COL_ALERT_STATUS = 55;
    private static final int COL_ALERT_CREATED_AT = 56;

    private static final int COL_WEEK_OF_YEAR = 57;
    private static final int COL_IS_MONTH_START = 58;
    private static final int COL_IS_MONTH_END = 59;
    private static final int COL_PREVIOUS_DAY_DEMAND = 60;
    private static final int COL_PREVIOUS_3_DAY_AVERAGE = 61;
    private static final int COL_PREVIOUS_7_DAY_AVERAGE = 62;
    private static final int COL_PREVIOUS_DAY_HOURS = 63;
    private static final int COL_TARGET_DEMAND = 64;


    // ============================================================
    // MAIN IMPORT METHOD
    // ============================================================

    /**
     * Imports the cleaned 65-column workforce CSV.
     *
     * This method:
     *
     * 1. Creates/updates Employee records
     * 2. Creates Attendance records
     * 3. Creates PredictionResult records
     * 4. Creates Alert records
     * 5. Creates CapacityUtilization records
     * 6. Creates PerformanceMetric records
     * 7. Creates PredictionRun
     *
     * Employees are collected in memory first so that the same
     * employee is not repeatedly written to the database for
     * every attendance/prediction row.
     */
    @Transactional
    public String importWorkforceCSV(
            File file,
            String originalFileName
    ) {

        log.info("=================================================");
        log.info("STARTING WORKFORCE CSV IMPORT");
        log.info("File: {}", originalFileName);
        log.info("=================================================");


        // ========================================================
        // COLLECTIONS
        // ========================================================

        List<EmployeeAttendance> attendanceList =
                new ArrayList<>();

        List<Alert> alerts =
                new ArrayList<>();

        List<CapacityUtilization> capacityUtilizations =
                new ArrayList<>();

        List<PerformanceMetric> performanceMetrics =
                new ArrayList<>();

        List<PredictionResult> predictionResults =
                new ArrayList<>();


        /*
         * One Employee object per EmployeeID.
         *
         * This is extremely important for performance.
         *
         * Example:
         *
         * Employee E001 appears 500 times in CSV
         *
         * We create only ONE Employee object for E001.
         */
        Map<String, Employee> employeesById =
                new LinkedHashMap<>();


        // ========================================================
        // CREATE PREDICTION RUN
        // ========================================================

        PredictionRun predictionRun =
                PredictionRun.builder()
                        .modelName("CSV Import")
                        .uploadedFile(originalFileName)
                        .totalRecords(0)
                        .averagePrediction(0.0)
                        .maximumPrediction(0.0)
                        .minimumPrediction(0.0)
                        .createdAt(LocalDateTime.now())
                        .build();

        predictionRun =
                predictionRunRepository.save(predictionRun);


        // ========================================================
        // STATISTICS
        // ========================================================

        int lineNumber = 0;

        int recordCount = 0;

        double totalPrediction = 0.0;

        double maxPrediction =
                -Double.MAX_VALUE;

        double minPrediction =
                Double.MAX_VALUE;


        // ========================================================
        // READ FILE
        // ========================================================

        try (
                BufferedReader br =
                        new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(file),
                                        StandardCharsets.UTF_8
                                )
                        )
        ) {

            String line;

            boolean isHeader = true;


            // ====================================================
            // LOOP THROUGH CSV
            // ====================================================

            while ((line = br.readLine()) != null) {

                lineNumber++;


                // ------------------------------------------------
                // HEADER
                // ------------------------------------------------

                if (isHeader) {

                    isHeader = false;

                    continue;
                }


                // ------------------------------------------------
                // EMPTY LINE
                // ------------------------------------------------

                if (line.trim().isEmpty()) {

                    continue;
                }


                // ------------------------------------------------
                // SPLIT CSV
                // ------------------------------------------------

                String[] values =
                        line.split(",", -1);


                // ------------------------------------------------
                // VALIDATE COLUMN COUNT
                // ------------------------------------------------

                if (values.length < EXPECTED_COLUMNS) {

                    log.warn(
                            "Skipping line {} - only {} columns found. Expected {}",
                            lineNumber,
                            values.length,
                            EXPECTED_COLUMNS
                    );

                    continue;
                }


                try {

                    // ====================================================
                    // 1. EMPLOYEE
                    // ====================================================

                    String employeeId =
                            clean(
                                    values[COL_EMPLOYEE_ID]
                            );


                    if (employeeId.isEmpty()) {

                        log.warn(
                                "Skipping line {} - EmployeeID is empty",
                                lineNumber
                        );

                        continue;
                    }


                    /*
                     * Find employee in memory.
                     *
                     * DO NOT query the database for every CSV row.
                     */
                    Employee employee =
                            employeesById.get(employeeId);


                    // ====================================================
                    // CREATE EMPLOYEE
                    // ====================================================

                    if (employee == null) {

                        employee =
                                Employee.builder()

                                        .employeeId(
                                                employeeId
                                        )

                                        /*
                                         * The cleaned CSV does not
                                         * contain an employee name.
                                         *
                                         * Therefore we use Employee ID
                                         * as a temporary display name.
                                         */
                                        .name(
                                                "Employee "
                                                        + employeeId
                                        )

                                        .gender(
                                                clean(
                                                        values[
                                                                COL_GENDER
                                                                ]
                                                )
                                        )

                                        .age(
                                                parseDouble(
                                                        values[
                                                                COL_AGE
                                                                ]
                                                )
                                        )

                                        /*
                                         * Email is not available in
                                         * the cleaned CSV.
                                         */
                                        .email(null)

                                        /*
                                         * Phone is not available.
                                         */
                                        .phoneNumber(null)

                                        .department(
                                                clean(
                                                        values[
                                                                COL_DEPARTMENT
                                                                ]
                                                )
                                        )

                                        .role(
                                                clean(
                                                        values[
                                                                COL_JOB_ROLE
                                                                ]
                                                )
                                        )

                                        .employmentType(
                                                clean(
                                                        values[
                                                                COL_EMPLOYMENT_TYPE
                                                                ]
                                                )
                                        )

                                        .team(
                                                clean(
                                                        values[
                                                                COL_TEAM
                                                                ]
                                                )
                                        )

                                        /*
                                         * Manager is not available
                                         * in the cleaned CSV.
                                         */
                                        .manager(null)

                                        .branch(
                                                clean(
                                                        values[
                                                                COL_BRANCH
                                                                ]
                                                )
                                        )

                                        .location(
                                                clean(
                                                        values[
                                                                COL_LOCATION
                                                                ]
                                                )
                                        )

                                        .shift(
                                                clean(
                                                        values[
                                                                COL_SHIFT
                                                                ]
                                                )
                                        )

                                        .preferredShift(
                                                clean(
                                                        values[
                                                                COL_PREFERRED_SHIFT
                                                                ]
                                                )
                                        )

                                        .experienceYears(
                                                parseDouble(
                                                        values[
                                                                COL_EXPERIENCE_YEARS
                                                                ]
                                                )
                                        )

                                        /*
                                         * Hire date is not available
                                         * in the cleaned CSV.
                                         */
                                        .joinDate(null)

                                        .status(
                                                getEmployeeStatus(
                                                        values
                                                )
                                        )

                                        .attendance(
                                                clean(
                                                        values[
                                                                COL_ATTENDANCE_STATUS
                                                                ]
                                                )
                                        )

                                        .utilization(
                                                parseDouble(
                                                        values[
                                                                COL_UTILIZATION_RATE
                                                                ]
                                                )
                                        )

                                        .lastUpdated(
                                                LocalDateTime.now()
                                        )

                                        .build();


                        employeesById.put(
                                employeeId,
                                employee
                        );
                    }


                    // ====================================================
                    // UPDATE LATEST EMPLOYEE INFORMATION
                    // ====================================================

                    else {

                        employee.setGender(
                                clean(
                                        values[
                                                COL_GENDER
                                                ]
                                )
                        );

                        employee.setAge(
                                parseDouble(
                                        values[
                                                COL_AGE
                                                ]
                                )
                        );

                        employee.setDepartment(
                                clean(
                                        values[
                                                COL_DEPARTMENT
                                                ]
                                )
                        );

                        employee.setRole(
                                clean(
                                        values[
                                                COL_JOB_ROLE
                                                ]
                                )
                        );

                        employee.setEmploymentType(
                                clean(
                                        values[
                                                COL_EMPLOYMENT_TYPE
                                                ]
                                )
                        );

                        employee.setTeam(
                                clean(
                                        values[
                                                COL_TEAM
                                                ]
                                )
                        );

                        employee.setBranch(
                                clean(
                                        values[
                                                COL_BRANCH
                                                ]
                                )
                        );

                        employee.setLocation(
                                clean(
                                        values[
                                                COL_LOCATION
                                                ]
                                )
                        );

                        employee.setShift(
                                clean(
                                        values[
                                                COL_SHIFT
                                                ]
                                )
                        );

                        employee.setPreferredShift(
                                clean(
                                        values[
                                                COL_PREFERRED_SHIFT
                                                ]
                                )
                        );

                        employee.setExperienceYears(
                                parseDouble(
                                        values[
                                                COL_EXPERIENCE_YEARS
                                                ]
                                )
                        );

                        employee.setAttendance(
                                clean(
                                        values[
                                                COL_ATTENDANCE_STATUS
                                                ]
                                )
                        );


                        Double utilization =
                                parseDouble(
                                        values[
                                                COL_UTILIZATION_RATE
                                                ]
                                );


                        if (utilization != null) {

                            employee.setUtilization(
                                    utilization
                            );
                        }


                        employee.setStatus(
                                getEmployeeStatus(
                                        values
                                )
                        );


                        employee.setLastUpdated(
                                LocalDateTime.now()
                        );
                    }


                    // ====================================================
                    // 2. ATTENDANCE
                    // ====================================================

                    String attendanceDate =
                            clean(
                                    values[
                                            COL_ATTENDANCE_DATE
                                            ]
                            );


                    String attendanceStatus =
                            clean(
                                    values[
                                            COL_ATTENDANCE_STATUS
                                            ]
                            );


                    Double workingHours =
                            parseDouble(
                                    values[
                                            COL_WORKING_HOURS
                                            ]
                            );


                    if (
                            !attendanceDate.isEmpty()
                                    &&
                                    !attendanceStatus.isEmpty()
                    ) {

                        LocalDate parsedDate =
                                parseDate(
                                        attendanceDate
                                );


                        if (parsedDate != null) {

                            EmployeeAttendance attendance =
                                    EmployeeAttendance.builder()

                                            .employee(
                                                    employee
                                            )

                                            .date(
                                                    parsedDate
                                            )

                                            .status(
                                                    attendanceStatus
                                            )

                                            /*
                                             * The cleaned CSV does not
                                             * contain check-in/check-out
                                             * fields in the defined
                                             * structure.
                                             */
                                            .checkInTime(null)

                                            .checkOutTime(null)

                                            .hoursWorked(
                                                    workingHours != null
                                                            ? workingHours
                                                            : 0.0
                                            )

                                            .createdAt(
                                                    LocalDateTime.now()
                                            )

                                            .build();


                            attendanceList.add(
                                    attendance
                            );
                        }
                    }


                    // ====================================================
                    // 3. PREDICTION RESULT
                    // ====================================================

                    Double actualDemand =
                            parseDouble(
                                    values[
                                            COL_HISTORICAL_DEMAND
                                            ]
                            );


                    Double predictedDemand =
                            parseDouble(
                                    values[
                                            COL_WORKFORCE_DEMAND
                                            ]
                            );


                    if (
                            actualDemand != null
                                    &&
                                    predictedDemand != null
                                    &&
                                    !attendanceDate.isEmpty()
                    ) {

                        LocalDate predictionDate =
                                parseDate(
                                        attendanceDate
                                );


                        if (predictionDate != null) {

                            PredictionResult predictionResult =
                                    PredictionResult.builder()

                                            .attendanceDate(
                                                    predictionDate
                                            )

                                            .department(
                                                    clean(
                                                            values[
                                                                    COL_DEPARTMENT
                                                                    ]
                                                    )
                                            )

                                            .actualDemand(
                                                    actualDemand
                                            )

                                            .predictedDemand(
                                                    predictedDemand
                                            )

                                            .employee_name(
                                                    employee.getName()
                                            )

                                            .attendenceStatus(
                                                    attendanceStatus
                                            )

                                            .CapacityUtilization(
                                                    clean(
                                                            values[
                                                                    COL_CAPACITY_UTILIZATION
                                                                    ]
                                                    )
                                            )

                                            .alertStatus(
                                                    clean(
                                                            values[
                                                                    COL_ALERT_LEVEL
                                                                    ]
                                                    )
                                            )

                                            .customerOrders(
                                                    clean(
                                                            values[
                                                                    COL_CUSTOMER_ORDERS
                                                                    ]
                                                    )
                                            )

                                            .ProductivityScore(
                                                    clean(
                                                            values[
                                                                    COL_PRODUCTIVITY
                                                                    ]
                                                    )
                                            )

                                            .CapacityLoad(
                                                    clean(
                                                            values[
                                                                    COL_CAPACITY_LOAD
                                                                    ]
                                                    )
                                            )

                                            .ScalingEvents(
                                                    clean(
                                                            values[
                                                                    COL_SCALING_EVENTS
                                                                    ]
                                                    )
                                            )

                                            .predictionRun(
                                                    predictionRun
                                            )

                                            .build();


                            predictionResults.add(
                                    predictionResult
                            );


                            totalPrediction +=
                                    predictedDemand;


                            maxPrediction =
                                    Math.max(
                                            maxPrediction,
                                            predictedDemand
                                    );


                            minPrediction =
                                    Math.min(
                                            minPrediction,
                                            predictedDemand
                                    );


                            recordCount++;
                        }
                    }


                    // ====================================================
                    // 4. ALERT
                    // ====================================================

                    String alertType =
                            clean(
                                    values[
                                            COL_ALERT_TYPE
                                            ]
                            );


                    String alertSeverity =
                            clean(
                                    values[
                                            COL_ALERT_SEVERITY
                                            ]
                            );


                    String alertStatus =
                            clean(
                                    values[
                                            COL_ALERT_STATUS
                                            ]
                            );


                    String alertCreatedAt =
                            clean(
                                    values[
                                            COL_ALERT_CREATED_AT
                                            ]
                            );


                    /*
                     * Only create an alert when AlertType exists.
                     */
                    if (!alertType.isEmpty()) {

                        Alert alert =
                                Alert.builder()

                                        .title(
                                                alertType
                                        )

                                        .message(
                                                "Workforce alert generated from prediction data"
                                        )

                                        .severity(
                                                alertSeverity
                                        )

                                        .type(
                                                alertType
                                        )

                                        .isRead(
                                                "Resolved"
                                                        .equalsIgnoreCase(
                                                                alertStatus
                                                        )
                                        )

                                        .createdAt(
                                                alertCreatedAt.isEmpty()
                                                        ? LocalDateTime.now()
                                                        : parseDateTime(
                                                        alertCreatedAt
                                                )
                                        )

                                        .resolvedAt(
                                                "Resolved"
                                                        .equalsIgnoreCase(
                                                                alertStatus
                                                        )
                                                        ? LocalDateTime.now()
                                                        : null
                                        )

                                        .build();


                        alerts.add(
                                alert
                        );
                    }


                    // ====================================================
                    // 5. CAPACITY UTILIZATION
                    // ====================================================

                    Double capacityUtilization =
                            parseDouble(
                                    values[
                                            COL_CAPACITY_UTILIZATION
                                            ]
                            );


                    Double currentCapacity =
                            parseDouble(
                                    values[
                                            COL_CURRENT_CAPACITY
                                            ]
                            );


                    Double requiredCapacity =
                            parseDouble(
                                    values[
                                            COL_REQUIRED_CAPACITY
                                            ]
                            );


                    if (capacityUtilization != null) {

                        CapacityUtilization capacity =
                                CapacityUtilization.builder()

                                        .department(
                                                clean(
                                                        values[
                                                                COL_DEPARTMENT
                                                                ]
                                                )
                                        )

                                        .utilizationRate(
                                                capacityUtilization
                                        )

                                        .availableCapacity(
                                                currentCapacity != null
                                                        ? currentCapacity
                                                        : 0.0
                                        )

                                        .usedCapacity(
                                                requiredCapacity != null
                                                        ? requiredCapacity
                                                        : 0.0
                                        )

                                        .date(
                                                attendanceDate
                                        )

                                        .createdAt(
                                                LocalDateTime.now()
                                        )

                                        .build();


                        capacityUtilizations.add(
                                capacity
                        );
                    }


                    // ====================================================
                    // 6. PERFORMANCE METRIC
                    // ====================================================

                    Double productivity =
                            parseDouble(
                                    values[
                                            COL_PRODUCTIVITY
                                            ]
                            );


                    LocalDate parsedAttendanceDate =
                            parseDate(
                                    attendanceDate
                            );


                    if (
                            productivity != null
                                    &&
                                    parsedAttendanceDate != null
                    ) {

                        PerformanceMetric metric =
                                PerformanceMetric.builder()

                                        .metricName(
                                                "Productivity"
                                        )

                                        .value(
                                                productivity
                                        )

                                        .category(
                                                "Performance"
                                        )

                                        .department(
                                                clean(
                                                        values[
                                                                COL_DEPARTMENT
                                                                ]
                                                )
                                        )

                                        .period(
                                                "Daily"
                                        )

                                        .timestamp(
                                                parsedAttendanceDate
                                                        .atStartOfDay()
                                        )

                                        .createdAt(
                                                LocalDateTime.now()
                                        )

                                        .build();


                        performanceMetrics.add(
                                metric
                        );
                    }


                } catch (Exception e) {

                    log.error(
                            "Error processing CSV line {}: {}",
                            lineNumber,
                            e.getMessage(),
                            e
                    );
                }
            }


            // ========================================================
            // SAVE EMPLOYEES
            // ========================================================

            log.info(
                    "Preparing {} unique employees...",
                    employeesById.size()
            );


            /*
             * Load existing employees once.
             *
             * This avoids:
             *
             * SELECT employee WHERE employee_id = ?
             *
             * thousands of times.
             */
            List<Employee> existingEmployees =
                    employeeRepository.findAll();


            Map<String, Employee> existingEmployeeMap =
                    new HashMap<>();


            for (Employee existing :
                    existingEmployees) {

                if (
                        existing.getEmployeeId() != null
                                &&
                                !existing.getEmployeeId().isBlank()
                ) {

                    existingEmployeeMap.put(
                            existing.getEmployeeId(),
                            existing
                    );
                }
            }


            List<Employee> employeesToSave =
                    new ArrayList<>();


            for (
                    Employee csvEmployee :
                    employeesById.values()
            ) {

                Employee existing =
                        existingEmployeeMap.get(
                                csvEmployee.getEmployeeId()
                        );


                if (existing != null) {

                    /*
                     * Update existing employee.
                     */
                    copyEmployeeData(
                            csvEmployee,
                            existing
                    );


                    employeesToSave.add(
                            existing
                    );

                } else {

                    /*
                     * New employee.
                     */
                    employeesToSave.add(
                            csvEmployee
                    );
                }
            }


            log.info(
                    "Saving {} employees in batch...",
                    employeesToSave.size()
            );


            List<Employee> savedEmployees =
                    employeeRepository.saveAll(employeesToSave);

            employeeRepository.flush();

            Map<String, Employee> savedEmployeeMap =
                    new HashMap<>();

            for (Employee savedEmployee : savedEmployees) {

                if (savedEmployee.getEmployeeId() != null
                        && !savedEmployee.getEmployeeId().isBlank()) {

                    savedEmployeeMap.put(
                            savedEmployee.getEmployeeId(),
                            savedEmployee
                    );
                }
            }

            /*
             * IMPORTANT:
             *
             * attendanceList was created before employees were saved.
             *
             * Therefore the Employee objects inside attendanceList
             * may still be transient objects with id = null.
             *
             * Replace those references with the actual saved Employee
             * entities.
             */
            for (EmployeeAttendance attendance : attendanceList) {

                Employee csvEmployee =
                        attendance.getEmployee();

                if (csvEmployee == null) {
                    continue;
                }

                String employeeId =
                        csvEmployee.getEmployeeId();

                Employee savedEmployee =
                        savedEmployeeMap.get(employeeId);

                if (savedEmployee != null) {

                    attendance.setEmployee(
                            savedEmployee
                    );

                } else {

                    log.warn(
                            "Unable to find saved employee for attendance. EmployeeID: {}",
                            employeeId
                    );
                }
            }

            log.info(
                    "Employees saved successfully: {}",
                    savedEmployees.size()
            );


            log.info(
                    "Employees saved successfully: {}",
                    employeesToSave.size()
            );


            // ========================================================
            // UPDATE PREDICTION RUN
            // ========================================================

            if (recordCount > 0) {

                predictionRun.setTotalRecords(
                        recordCount
                );


                predictionRun.setAveragePrediction(
                        totalPrediction /
                                recordCount
                );


                predictionRun.setMaximumPrediction(
                        maxPrediction
                );


                predictionRun.setMinimumPrediction(
                        minPrediction
                );


                predictionRunRepository.save(
                        predictionRun
                );


                predictionRunRepository.flush();
            }


            // ========================================================
            // SAVE ATTENDANCE
            // ========================================================

            log.info(
                    "Saving attendance records: {}",
                    attendanceList.size()
            );


            if (!attendanceList.isEmpty()) {

                employeeAttendanceRepository.saveAll(
                        attendanceList
                );

                employeeAttendanceRepository.flush();
            }


            // ========================================================
            // SAVE ALERTS
            // ========================================================

            log.info(
                    "Saving alert records: {}",
                    alerts.size()
            );


            if (!alerts.isEmpty()) {

                alertRepository.saveAll(
                        alerts
                );

                alertRepository.flush();
            }


            // ========================================================
            // SAVE CAPACITY
            // ========================================================

            log.info(
                    "Saving capacity records: {}",
                    capacityUtilizations.size()
            );


            if (!capacityUtilizations.isEmpty()) {

                capacityUtilizationRepository.saveAll(
                        capacityUtilizations
                );

                capacityUtilizationRepository.flush();
            }


            // ========================================================
            // SAVE PERFORMANCE
            // ========================================================

            log.info(
                    "Saving performance records: {}",
                    performanceMetrics.size()
            );


            if (!performanceMetrics.isEmpty()) {

                performanceMetricRepository.saveAll(
                        performanceMetrics
                );

                performanceMetricRepository.flush();
            }


            // ========================================================
            // SAVE PREDICTIONS
            // ========================================================

            log.info(
                    "Saving prediction records: {}",
                    predictionResults.size()
            );


            if (!predictionResults.isEmpty()) {

                predictionResultRepository.saveAll(
                        predictionResults
                );

                predictionResultRepository.flush();
            }


            // ========================================================
            // FINAL LOG
            // ========================================================

            log.info(
                    "================================================="
            );

            log.info(
                    "CSV IMPORT COMPLETED"
            );

            log.info(
                    "Processed lines: {}",
                    lineNumber
            );

            log.info(
                    "Unique employees: {}",
                    employeesById.size()
            );

            log.info(
                    "Prediction records: {}",
                    predictionResults.size()
            );

            log.info(
                    "Attendance records: {}",
                    attendanceList.size()
            );

            log.info(
                    "Alert records: {}",
                    alerts.size()
            );

            log.info(
                    "Capacity records: {}",
                    capacityUtilizations.size()
            );

            log.info(
                    "Performance records: {}",
                    performanceMetrics.size()
            );

            log.info(
                    "================================================="
            );


            return
                    "CSV Import completed successfully. "
                            +
                            "Processed "
                            +
                            lineNumber
                            +
                            " lines and saved "
                            +
                            employeesById.size()
                            +
                            " unique employees.";


        } catch (Exception e) {

            log.error(
                    "CSV import failed",
                    e
            );


            throw new RuntimeException(
                    "Failed to import CSV: "
                            +
                            e.getMessage(),
                    e
            );
        }
    }


    // ============================================================
    // COPY EMPLOYEE DATA
    // ============================================================

    private void copyEmployeeData(
            Employee source,
            Employee target
    ) {

        /*
         * Do NOT change employeeId here.
         *
         * employeeId is our unique identifier.
         */

        target.setName(
                source.getName()
        );

        target.setGender(
                source.getGender()
        );

        target.setAge(
                source.getAge()
        );

        target.setEmail(
                source.getEmail()
        );

        target.setPhoneNumber(
                source.getPhoneNumber()
        );

        target.setDepartment(
                source.getDepartment()
        );

        target.setRole(
                source.getRole()
        );

        target.setEmploymentType(
                source.getEmploymentType()
        );

        target.setTeam(
                source.getTeam()
        );

        target.setManager(
                source.getManager()
        );

        target.setBranch(
                source.getBranch()
        );

        target.setLocation(
                source.getLocation()
        );

        target.setShift(
                source.getShift()
        );

        target.setPreferredShift(
                source.getPreferredShift()
        );

        target.setExperienceYears(
                source.getExperienceYears()
        );

        target.setJoinDate(
                source.getJoinDate()
        );

        target.setStatus(
                source.getStatus()
        );

        target.setAttendance(
                source.getAttendance()
        );

        target.setUtilization(
                source.getUtilization()
        );

        target.setLastUpdated(
                LocalDateTime.now()
        );
    }


    // ============================================================
    // EMPLOYEE STATUS
    // ============================================================

    private String getEmployeeStatus(
            String[] values
    ) {

        /*
         * WorkforceStatus is column 48.
         */
        String workforceStatus =
                clean(
                        values[
                                COL_WORKFORCE_STATUS
                                ]
                );


        if (!workforceStatus.isEmpty()) {

            return workforceStatus;
        }


        /*
         * If WorkforceStatus is empty,
         * use Active as the default.
         */
        return "Active";
    }


    // ============================================================
    // DATE PARSER
    // ============================================================

    private LocalDate parseDate(
            String value
    ) {

        if (
                value == null
                        ||
                        value.trim().isEmpty()
        ) {

            return null;
        }


        try {

            return LocalDate.parse(
                    clean(value),
                    DATE_FORMATTER
            );

        } catch (Exception e) {

            return null;
        }
    }


    // ============================================================
    // DOUBLE PARSER
    // ============================================================

    private Double parseDouble(
            String value
    ) {

        if (
                value == null
                        ||
                        value.trim().isEmpty()
        ) {

            return null;
        }


        try {

            return Double.parseDouble(
                    clean(value)
            );

        } catch (NumberFormatException e) {

            return null;
        }
    }


    // ============================================================
    // INTEGER PARSER
    // ============================================================

    private Integer parseInt(
            String value
    ) {

        if (
                value == null
                        ||
                        value.trim().isEmpty()
        ) {

            return null;
        }


        try {

            return Integer.parseInt(
                    clean(value)
            );

        } catch (NumberFormatException e) {

            return null;
        }
    }


    // ============================================================
    // DATETIME PARSER
    // ============================================================

    private LocalDateTime parseDateTime(
            String value
    ) {

        if (
                value == null
                        ||
                        value.trim().isEmpty()
        ) {

            return LocalDateTime.now();
        }


        try {

            return LocalDateTime.parse(
                    clean(value),
                    DATE_TIME_FORMATTER
            );

        } catch (Exception e) {

            log.debug(
                    "Unable to parse datetime: {}",
                    value
            );


            return LocalDateTime.now();
        }
    }


    // ============================================================
    // CLEAN CSV VALUE
    // ============================================================

    private String clean(
            String value
    ) {

        if (value == null) {

            return "";
        }


        return value
                .trim()
                .replace("\"", "");
    }
}