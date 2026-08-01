import numpy as np
import pandas as pd

from sklearn.preprocessing import MinMaxScaler, LabelEncoder


def prepare_lstm_data(df, sequence_length=14):

    print("\nPreparing LSTM Dataset...")

    # =====================================================
    # COPY DATA
    # =====================================================

    df = df.copy()

    # =====================================================
    # DATE CONVERSION
    # =====================================================

    df["AttendanceDate"] = pd.to_datetime(df["AttendanceDate"])

    # =====================================================
    # SORT BY DEPARTMENT & DATE
    # =====================================================

    df = df.sort_values(["Department", "AttendanceDate"]).reset_index(drop=True)

    # =====================================================
    # ENCODE DEPARTMENT
    # =====================================================

    department_encoder = LabelEncoder()

    df["Department"] = department_encoder.fit_transform(df["Department"])

    # =====================================================
    # FEATURES
    # =====================================================

    feature_columns = [
        # Historical Features
        "PreviousDayDemand",
        "Previous3DayAverage",
        "Previous7DayAverage",
        "PreviousDayHours",
        # Workforce Features
        "WorkingHours",
        "OvertimeHours",
        "HistoricalDemand",
        "CapacityLoad",
        "PeakUtilization",
        "AvailableHeadroom",
        # Performance
        "EfficiencyScore",
        "ProductivityScore",
        "AttendanceRate",
        # Calendar
        "Year",
        "Quarter",
        "Month",
        "WeekOfYear",
        "DayOfWeek",
        "Weekend",
        "IsMonthStart",
        "IsMonthEnd",
    ]

    feature_columns = [c for c in feature_columns if c in df.columns]

    print("\nLSTM Features")

    for col in feature_columns:
        print(col)

    # =====================================================
    # GLOBAL TARGET SCALER
    # =====================================================

    training_targets = []

    for _, group in df.groupby("Department"):

        group = group.reset_index(drop=True)

        if len(group) <= sequence_length:
            continue

        split_index = int(len(group) * 0.80)

        training_targets.append(group.iloc[:split_index][["TargetDemand"]])

    training_targets = pd.concat(
        training_targets,
        ignore_index=True,
    )

    target_scaler = MinMaxScaler()

    target_scaler.fit(training_targets)

    # =====================================================
    # CREATE TRAIN / TEST SETS
    # =====================================================

    X_train = []
    X_test = []
    X_dashboard = []

    y_train = []
    y_test = []

    test_departments = []
    for _, group in df.groupby("Department"):

        group = group.reset_index(drop=True)

        if len(group) <= sequence_length:
            continue

        split_index = int(len(group) * 0.80)

        train_group = group.iloc[:split_index].copy()

        test_group = group.iloc[split_index:].copy()

        # =================================================
        # FEATURE SCALER
        # =================================================

        feature_scaler = MinMaxScaler()

        feature_scaler.fit(train_group[feature_columns])

        # Convert feature columns to float before scaling
        train_group[feature_columns] = train_group[feature_columns].astype(np.float32)

        test_group[feature_columns] = test_group[feature_columns].astype(np.float32)
        train_group[feature_columns] = feature_scaler.transform(
            train_group[feature_columns]
        ).astype(np.float32)

        test_group[feature_columns] = feature_scaler.transform(
            test_group[feature_columns]
        ).astype(np.float32)

        # =================================================
        # TARGET SCALING
        # =================================================

        train_group["TargetDemand"] = (
            target_scaler.transform(train_group[["TargetDemand"]])
            .astype(np.float32)
            .ravel()
        )

        test_group["TargetDemand"] = (
            target_scaler.transform(test_group[["TargetDemand"]])
            .astype(np.float32)
            .ravel()
        )

        # =================================================
        # TRAINING SEQUENCES
        # =================================================

        train_features = train_group[feature_columns].to_numpy(dtype=np.float32)

        train_targets = train_group["TargetDemand"].to_numpy(dtype=np.float32)

        for i in range(
            sequence_length,
            len(train_group),
        ):

            X_train.append(train_features[i - sequence_length : i])

            y_train.append(train_targets[i])

        # =================================================
        # TEST SEQUENCES
        # =================================================

        combined = pd.concat(
            [
                train_group.tail(sequence_length),
                test_group,
            ],
            ignore_index=True,
        )

        combined_features = combined[feature_columns].to_numpy(dtype=np.float32)

        combined_targets = combined["TargetDemand"].to_numpy(dtype=np.float32)

        for i in range(
            sequence_length,
            len(combined),
        ):

            X_test.append(combined_features[i - sequence_length : i])

            y_test.append(combined_targets[i])

            test_departments.append(combined.iloc[i]["Department"])
            # =================================================
        # DASHBOARD SEQUENCES
        # =================================================

        full_group = group.copy()

        full_group[feature_columns] = full_group[feature_columns].astype(np.float32)

        full_group[feature_columns] = feature_scaler.transform(
            full_group[feature_columns]
        ).astype(np.float32)

        full_features = full_group[feature_columns].to_numpy(dtype=np.float32)

        for i in range(sequence_length, len(full_group)):

            X_dashboard.append(full_features[i - sequence_length : i])

    # =====================================================
    # CONVERT TO NUMPY
    # =====================================================

    X_train = np.asarray(
        X_train,
        dtype=np.float32,
    )

    X_test = np.asarray(
        X_test,
        dtype=np.float32,
    )
    X_dashboard = np.asarray(
        X_dashboard,
        dtype=np.float32,
    )

    y_train = np.asarray(
        y_train,
        dtype=np.float32,
    )

    y_test = np.asarray(
        y_test,
        dtype=np.float32,
    )
    test_departments = np.asarray(test_departments)
    # =====================================================
    # DATA SUMMARY
    # =====================================================

    print(f"\nLSTM Training Samples : {len(X_train)}")
    print(f"LSTM Testing Samples  : {len(X_test)}")
    print(f"Input Shape : {X_train.shape}")

    print("\nFeature Summary")
    print(f"Sequence Length : {sequence_length}")
    print(f"Number of Features : {len(feature_columns)}")

    # =====================================================
    # VALIDATION
    # =====================================================

    if len(X_train) == 0:
        raise ValueError(
            "No LSTM training sequences were generated. "
            "Check your dataset or sequence length."
        )

    if len(X_test) == 0:
        raise ValueError(
            "No LSTM testing sequences were generated. "
            "Check your dataset or sequence length."
        )

    # =====================================================
    # RETURN
    # =====================================================
    print("\n========== LSTM DEBUG ==========")
    print("X_train shape:", X_train.shape)
    print("X_test shape :", X_test.shape)
    print("y_train shape:", y_train.shape)
    print("y_test shape :", y_test.shape)
    print("X_dashboard shape:", X_dashboard.shape)

    print("X_train dtype:", X_train.dtype)
    print("X_test dtype :", X_test.dtype)

    print("Sample X_train:", X_train[0].shape)
    print("================================")

    return (
        X_train,
        X_test,
        y_train,
        y_test,
        X_dashboard,
        target_scaler,
        test_departments,
    )
