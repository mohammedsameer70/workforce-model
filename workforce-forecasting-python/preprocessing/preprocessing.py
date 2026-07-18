import pandas as pd

from sklearn.preprocessing import LabelEncoder
from sklearn.model_selection import train_test_split


# ==============================
# Column Lists
# ==============================

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

drop_unnecessary_columns = [
    "DateOfBirth",
    "ClockInTime",
    "ClockOutTime",
    "WorkforceStatus",
    "ProcessingStatus"
]


# ==============================
# Load Dataset
# ==============================

def load_dataset(file_path):
    """
    Load dataset from CSV file.
    """
    return pd.read_csv(file_path)


# ==============================
# Inspect Dataset
# ==============================

def inspect_dataset(df):
    """
    Display dataset information.
    """

    print("\n========== DATASET INFORMATION ==========\n")

    print("Dataset Shape:", df.shape)

    print("\nColumns:")
    print(df.columns.tolist())

    print("\nData Types:")
    print(df.dtypes)

    print("\nDuplicate Rows:")
    print(df.duplicated().sum())

    print("\nMissing Values:")
    missing = df.isnull().sum()
    print(missing[missing > 0])

    print("\n=========================================\n")


# ==============================
# Clean Dataset
# ==============================

def clean_dataset(df):
    """
    Clean dataset and prepare ML dataset.
    """

    print("\n========== CLEANING DATASET ==========\n")

    # Fill missing values
    df["SpecialEvent"] = df["SpecialEvent"].fillna("No Event")

    for column in attendance_columns:
        df[column] = df[column].fillna(0)

    # Convert dates
    for column in date_columns:
        df[column] = pd.to_datetime(df[column])

    # Create ML copy
    ml_df = df.copy()

    # Remove identifier columns
    ml_df.drop(columns=drop_identifier_columns, inplace=True)

    print("Cleaning Completed Successfully!\n")

    print("Original Dataset Shape :", df.shape)
    print("ML Dataset Shape       :", ml_df.shape)

    print("\nRemaining Missing Values:")
    print(ml_df.isnull().sum()[ml_df.isnull().sum() > 0])

    return df, ml_df


# ==============================
# Encode Dataset
# ==============================

def encode_dataset(ml_df):
    """
    Encode categorical variables.
    """

    print("\n========== ENCODING DATASET ==========\n")

    # Remove unnecessary columns
    ml_df.drop(columns=drop_unnecessary_columns, inplace=True)

    # Label Encoding
    label_encoder = LabelEncoder()

    binary_columns = [
        "Gender",
        "EmploymentType",
        "PublicHoliday",
        "Weekend"
    ]

    for column in binary_columns:
        ml_df[column] = label_encoder.fit_transform(ml_df[column])

    # Manual encoding
    alert_mapping = {
        "Low": 0,
        "Medium": 1,
        "High": 2
    }

    ml_df["AlertLevel"] = ml_df["AlertLevel"].map(alert_mapping)

    # One-Hot Encoding
    one_hot_columns = [
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
        columns=one_hot_columns,
        drop_first=True,
        dtype=int
    )

    print("Encoding Completed Successfully!")

    print("Final Dataset Shape:", ml_df.shape)

    return ml_df


# ==============================
# Split Features and Target
# ==============================

def split_features_target(ml_df):
    """
    Separate features and target variable.
    """

    X = ml_df.drop(columns=["WorkforceDemand"])
    y = ml_df["WorkforceDemand"]

    return X, y


# ==============================
# Train Test Split
# ==============================

def split_train_test(X, y):
    """
    Split dataset into training and testing sets.
    """

    X_train, X_test, y_train, y_test = train_test_split(
        X,
        y,
        test_size=0.2,
        random_state=42
    )

    return X_train, X_test, y_train, y_test


# ==============================
# Main
# ==============================

if __name__ == "__main__":

    df = load_dataset("../dataset/workforce_forecasting_dataset_2024.csv")

    inspect_dataset(df)

    df, ml_df = clean_dataset(df)

    ml_df = encode_dataset(ml_df)

    X, y = split_features_target(ml_df)

    X_train, X_test, y_train, y_test = split_train_test(X, y)

    print("\n========== FINAL DATA ==========\n")

    print("Training Features :", X_train.shape)
    print("Testing Features  :", X_test.shape)

    print("Training Target   :", y_train.shape)
    print("Testing Target    :", y_test.shape)