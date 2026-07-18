import pandas as pd

from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder


# =====================================
# Columns
# =====================================

attendance_columns = [
    "WorkingHours",
    "BreakHours",
    "OvertimeHours",
    "LateMinutes",
    "EarlyDepartureMinutes",
    "ProductivityScore",
    "PerformanceRating",
    "UtilizationRate",
    "CapacityUtilization",
    "EfficiencyScore"
]

date_columns = [
    "DateOfBirth",
    "HireDate",
    "AttendanceDate",
    "LastUpdated"
]

drop_identifier_columns = [
    "EmployeeID",
    "FirstName",
    "LastName",
    "Email",
    "PhoneNumber"
]

drop_columns = [
    "DateOfBirth",
    "ClockInTime",
    "ClockOutTime",
    "WorkforceStatus",
    "ProcessingStatus"
]


# =====================================
# Load Dataset
# =====================================

def load_dataset(file_path):
    print("\nLoading Dataset...\n")
    return pd.read_csv(file_path)


# =====================================
# Inspect Dataset
# =====================================

def inspect_dataset(df):

    print("=" * 50)
    print("DATASET INFORMATION")
    print("=" * 50)

    print(f"\nShape : {df.shape}")

    print("\nData Types")
    print(df.dtypes)

    print("\nMissing Values")
    print(df.isnull().sum()[df.isnull().sum() > 0])

    print("\nDuplicate Rows")
    print(df.duplicated().sum())

    print("=" * 50)


# =====================================
# Clean Dataset
# =====================================

def clean_dataset(df):

    print("\nCleaning Dataset...\n")

    # Fill attendance values with 0
    for column in attendance_columns:
        df[column] = df[column].fillna(0)

    # Fill Special Event
    df["SpecialEvent"] = df["SpecialEvent"].fillna("No Event")

    # Convert dates
    for column in date_columns:
        df[column] = pd.to_datetime(df[column])

    # Create ML dataset
    ml_df = df.copy()

    # Remove identifiers
    ml_df.drop(columns=drop_identifier_columns, inplace=True)

    print("Cleaning Completed.")

    return df, ml_df


# =====================================
# Encode Dataset
# =====================================

def encode_dataset(ml_df):

    print("\nEncoding Dataset...\n")

    # Remove unnecessary columns
    ml_df.drop(columns=drop_columns, inplace=True)

    # Binary Encoding
    binary_columns = [
        "Gender",
        "EmploymentType",
        "PublicHoliday",
        "Weekend"
    ]

    encoder = LabelEncoder()

    for column in binary_columns:
        ml_df[column] = encoder.fit_transform(ml_df[column])

    # Alert Level Mapping
    ml_df["AlertLevel"] = ml_df["AlertLevel"].map({
        "Low": 0,
        "Medium": 1,
        "High": 2
    })

    # One Hot Encoding
    categorical_columns = [
        "Department",
        "JobRole",
        "Team",
        "Manager",
        "Branch",
        "Location",
        "DayOfWeek",
        "Shift",
        "PreferredShift",
        "AttendanceStatus",
        "WeatherCondition",
        "Season",
        "SpecialEvent",
        "NotificationType"
    ]

    ml_df = pd.get_dummies(
        ml_df,
        columns=categorical_columns,
        drop_first=True,
        dtype=int
    )

    print("Encoding Completed.")
    print(f"Dataset Shape : {ml_df.shape}")

    return ml_df


# =====================================
# Split Features and Target
# =====================================

def split_features_target(ml_df):

    X = ml_df.drop(columns=["WorkforceDemand"])
    y = ml_df["WorkforceDemand"]

    return X, y


# =====================================
# Train Test Split
# =====================================

def split_train_test(X, y):

    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        test_size=0.20,
        random_state=42
    )

    return X_train, X_test, y_train, y_test


# =====================================
# Main (Testing)
# =====================================

if __name__ == "__main__":

    df = load_dataset("../dataset/workforce_forecasting_dataset_2024.csv")

    inspect_dataset(df)

    df, ml_df = clean_dataset(df)

    ml_df = encode_dataset(ml_df)

    X, y = split_features_target(ml_df)

    X_train, X_test, y_train, y_test = split_train_test(X, y)

    print("\nFinal Shapes")
    print("------------------------")
    print("X Train :", X_train.shape)
    print("X Test  :", X_test.shape)
    print("y Train :", y_train.shape)
    print("y Test  :", y_test.shape)