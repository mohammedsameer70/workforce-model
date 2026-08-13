export interface TrainingRequestDTO {
    file: File;
    algorithms: string[];
}

export interface TrainingMetricsDTO {
    RMSE: number;
    R2: number;
}

export interface TrainingComparisonItemDTO {
    Model: string;
    RMSE: number;
    MAE: number;
    MAPE: number;
    R2: number;
}

export interface TrainingResponseDTO {
    fileName: string;
    bestModel: string;
    status: string;
    actions: string[];
    metrics: TrainingMetricsDTO;
    comparison?: TrainingComparisonItemDTO[];
}

export interface TrainingResultDTO extends TrainingResponseDTO {
    rmse: number;
    r2: number;
}

export interface TrainingHistoryDTO {
    date: string;
    dataset: string;
    algorithmsUsed: string[];
    bestModel: string;
    rmse: number;
    r2: number;
    status: string;
    actions: string[];
}

export interface ModelComparisonDTO {
    name: string;
    rmse: number;
    mae: number;
    mape: number;
    r2: number;
    trainingTime: string;
    status: string;
}
