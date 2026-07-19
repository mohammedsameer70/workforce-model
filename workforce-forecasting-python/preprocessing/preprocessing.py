import pandas as pd

# =====================================================
# LOAD DATASET
# =====================================================


def load_dataset(path):

    print("\nLoading Workforce Forecasting Dataset...")

    df = pd.read_csv(path)

    return df


# =====================================================
# INSPECT DATASET
# =====================================================


def inspect_dataset(df):

    print("\n" + "=" * 60)
    print("WORKFORCE FORECASTING DATASET")
    print("=" * 60)

    print(f"\nShape : {df.shape}")

    print("\nColumns")
    print(df.columns.tolist())

    print("\nData Types")
    print(df.dtypes)

    print("\nMissing Values")
    print(df.isnull().sum())

    print("\nDuplicate Rows")
    print(df.duplicated().sum())


# =====================================================
# PREPROCESS DATASET
# =====================================================


def preprocess_dataset(df):

    print("\nPreprocessing Dataset...")

    df = df.copy()

    # -------------------------------------------------
    # Remove duplicates
    # -------------------------------------------------

    df.drop_duplicates(inplace=True)

    # -------------------------------------------------
    # Convert Date
    # -------------------------------------------------

    df["AttendanceDate"] = pd.to_datetime(df["AttendanceDate"])

    # -------------------------------------------------
    # Sort Data
    # -------------------------------------------------

    df = df.sort_values(["Department", "AttendanceDate"]).reset_index(drop=True)

    # -------------------------------------------------
    # Calendar Features
    # -------------------------------------------------

    df["Year"] = df["AttendanceDate"].dt.year
    df["Quarter"] = df["AttendanceDate"].dt.quarter
    df["Month"] = df["AttendanceDate"].dt.month
    df["WeekOfYear"] = df["AttendanceDate"].dt.isocalendar().week.astype(int)
    df["DayOfWeek"] = df["AttendanceDate"].dt.dayofweek

    df["Weekend"] = (df["DayOfWeek"] >= 5).astype(int)

    df["IsMonthStart"] = df["AttendanceDate"].dt.is_month_start.astype(int)

    df["IsMonthEnd"] = df["AttendanceDate"].dt.is_month_end.astype(int)

    # -------------------------------------------------
    # Historical Features
    # -------------------------------------------------

    dept = df.groupby("Department")

    df["PreviousDayDemand"] = dept["WorkforceDemand"].shift(1)

    df["Previous3DayAverage"] = dept["WorkforceDemand"].transform(
        lambda x: x.shift(1).rolling(3, min_periods=1).mean()
    )

    df["Previous7DayAverage"] = dept["WorkforceDemand"].transform(
        lambda x: x.shift(1).rolling(7, min_periods=1).mean()
    )

    if "WorkingHours" in df.columns:
        df["PreviousDayHours"] = dept["WorkingHours"].shift(1)

    if "EmployeesOnLeave" in df.columns:
        df["PreviousLeaveCount"] = dept["EmployeesOnLeave"].shift(1)

    # -------------------------------------------------
    # Forecast Target
    # -------------------------------------------------

    df["TargetDemand"] = dept["WorkforceDemand"].shift(-1)

    # -------------------------------------------------
    # Remove First/Last Rows Created by Shift
    # -------------------------------------------------
    print("\nRows before dropna:", len(df))

    print("\nMissing values by column:")
    print(df.isnull().sum()[df.isnull().sum() > 0].sort_values(ascending=False))
    required_columns = [
        "TargetDemand",
        "PreviousDayDemand",
        "Previous3DayAverage",
        "Previous7DayAverage",
    ]

    if "PreviousDayHours" in df.columns:
        required_columns.append("PreviousDayHours")

    df.dropna(subset=required_columns, inplace=True)
    print("\nRows after dropna:", len(df))

    # -------------------------------------------------
    # Remove Identifier Columns
    # -------------------------------------------------

    columns_to_drop = [
        "EmployeeID",
        "EmployeeName",
        "Supervisor",
        "Timestamp",
        "ClockIn",
        "ClockOut",
        "Remarks",
    ]

    existing_columns = [col for col in columns_to_drop if col in df.columns]

    df.drop(columns=existing_columns, inplace=True, errors="ignore")

    print("\nDataset preprocessing completed.")

    print(f"Final Shape : {df.shape}")
    # -------------------------------------------------

    # Fill Remaining Missing Numeric Values
    # -------------------------------------------------

    numeric_columns = df.select_dtypes(include=["number"]).columns

    for col in numeric_columns:
        if df[col].isnull().sum() > 0:
            df[col] = df[col].fillna(df[col].median())

    return df


# =====================================================
# ENCODE DATASET
# =====================================================


def encode_dataset(df):

    print("\nEncoding Dataset...")

    df = df.copy()

    categorical_columns = df.select_dtypes(include=["object"]).columns.tolist()

    if "AttendanceDate" in categorical_columns:
        categorical_columns.remove("AttendanceDate")

    if len(categorical_columns) > 0:

        df = pd.get_dummies(df, columns=categorical_columns, drop_first=True)

    print("Encoding completed.")
    print("\nRemaining Missing Values")
    print(df.isnull().sum()[df.isnull().sum() > 0].sort_values(ascending=False))

    return df


# =====================================================
# SPLIT FEATURES & TARGET
# =====================================================


def split_features_target(df):

    X = df.drop(
        columns=[
            "AttendanceDate",
            "TargetDemand",
        ],
        errors="ignore",
    )

    y = df["TargetDemand"]

    return X, y


# =====================================================
# TRAIN TEST SPLIT
# =====================================================


def split_train_test(X, y):

    print("\nSplitting Dataset (Chronological 80/20)...")

    split_index = int(len(X) * 0.80)

    X_train = X.iloc[:split_index].copy()
    X_test = X.iloc[split_index:].copy()

    y_train = y.iloc[:split_index].copy()
    y_test = y.iloc[split_index:].copy()

    print(f"Training Samples : {len(X_train)}")
    print(f"Testing Samples  : {len(X_test)}")

    return (
        X_train,
        X_test,
        y_train,
        y_test,
    )
