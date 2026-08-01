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

        try (CSVReader reader = new CSVReader(new FileReader(predictionCsvPath))) {

            // Skip Header
            reader.readNext();

            System.out.println("Working Directory: " + System.getProperty("user.dir"));
            System.out.println("Prediction CSV Path: " + predictionCsvPath);

            String[] row;

            while ((row = reader.readNext()) != null) {


                PredictionRecord record = new PredictionRecord();

                record.setAttendanceDate(row[16]);
                record.setDepartment(row[5]);
                record.setTeam(row[10]);
                record.setShift(row[21]);

                record.setDayOfWeek(Integer.parseInt(row[17]));
                record.setMonth(Integer.parseInt(row[18]));
                record.setQuarter(Integer.parseInt(row[19]));
                record.setYear(Integer.parseInt(row[20]));

                record.setProductivityScore(parseDouble(row[31], "ProductivityScore"));
                record.setUtilizationRate(parseDouble(row[36], "UtilizationRate"));
                record.setCapacityUtilization(parseDouble(row[37], "CapacityUtilization"));
                record.setEfficiencyScore(parseDouble(row[38], "EfficiencyScore"));

                record.setCurrentCapacity(parseDouble(row[44], "CurrentCapacity"));
                record.setRequiredCapacity(parseDouble(row[45], "RequiredCapacity"));
                record.setAvailableHeadroom(parseDouble(row[46], "AvailableHeadroom"));
                record.setCapacityLoad(parseDouble(row[47], "CapacityLoad"));

                record.setPeakUtilization(parseDouble(row[48], "PeakUtilization"));
                record.setScalingEvents(parseDouble(row[49], "ScalingEvents"));

                record.setHistoricalDemand(parseDouble(row[55], "HistoricalDemand"));
                record.setWorkforceDemand(parseDouble(row[56], "WorkforceDemand"));

                record.setWorkforceStatus(row[57]);

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