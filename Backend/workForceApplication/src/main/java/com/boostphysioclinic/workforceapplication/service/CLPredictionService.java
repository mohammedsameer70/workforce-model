package com.boostphysioclinic.workforceapplication.service;

import com.boostphysioclinic.workforceapplication.dto.PredictionRecord;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class CLPredictionService {

    @Value("${prediction.csv.path}")
    private String predictionCsvPath;

    public List<PredictionRecord> getPredictions()
            throws IOException, CsvValidationException {

        List<PredictionRecord> predictions = new ArrayList<>();

        // Resolve relative path from working directory
        String csvPath = predictionCsvPath;
        if (!csvPath.startsWith("/") && !csvPath.contains(":")) {
            // Relative path - resolve from working directory
            String workingDir = System.getProperty("user.dir");
            csvPath = workingDir + "/" + csvPath;
        }

        System.out.println("Working Directory: " + System.getProperty("user.dir"));
        System.out.println("Prediction CSV Path: " + csvPath);

        try (CSVReader reader = new CSVReader(new FileReader(csvPath))) {

            // Skip Header
            reader.readNext();

            String[] row;

            while ((row = reader.readNext()) != null) {


                PredictionRecord record = new PredictionRecord();

                record.setAttendanceDate(row[10]);
                record.setDepartment(row[3]);
                record.setTeam(row[7]);
                record.setShift(row[15]);

                record.setDayOfWeek(Integer.parseInt(row[11]));
                record.setMonth(Integer.parseInt(row[12]));
                record.setQuarter(Integer.parseInt(row[13]));
                record.setYear(Integer.parseInt(row[14]));

                record.setProductivityScore(parseDouble(row[23], "ProductivityScore"));
                record.setUtilizationRate(parseDouble(row[28], "UtilizationRate"));
                record.setCapacityUtilization(parseDouble(row[29], "CapacityUtilization"));
                record.setEfficiencyScore(parseDouble(row[30], "EfficiencyScore"));

                record.setCurrentCapacity(parseDouble(row[36], "CurrentCapacity"));
                record.setRequiredCapacity(parseDouble(row[37], "RequiredCapacity"));
                record.setAvailableHeadroom(parseDouble(row[38], "AvailableHeadroom"));
                record.setCapacityLoad(parseDouble(row[39], "CapacityLoad"));

                record.setPeakUtilization(parseDouble(row[40], "PeakUtilization"));
                record.setScalingEvents(parseDouble(row[41], "ScalingEvents"));

                record.setHistoricalDemand(parseDouble(row[46], "HistoricalDemand"));
                record.setWorkforceDemand(parseDouble(row[47], "WorkforceDemand"));

                record.setWorkforceStatus(row[48]);

                // Last column = PredictedDemand
                record.setPredictedDemand(
                        parseDouble(row[row.length - 1], "PredictedDemand")
                );

                predictions.add(record);
            }
        }

        return predictions;
    }

    /**
     * Debug parser to identify the exact column causing errors.
     */
    private double parseDouble(String value, String field) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {

            System.out.println("=====================================");
            System.out.println("ERROR PARSING FIELD");
            System.out.println("Field : " + field);
            System.out.println("Value : " + value);
            System.out.println("=====================================");

            throw new RuntimeException(
                    "Failed parsing field '" + field + "' with value '" + value + "'",
                    ex
            );
        }
    }
}