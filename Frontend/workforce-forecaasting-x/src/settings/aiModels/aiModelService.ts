import api from "@/services/apiClient";
import type { TrainingResponseDTO } from "./trainingDTO";

class AIModelService {

    async train(formData: FormData, signal?: AbortSignal): Promise<TrainingResponseDTO> {
        const response = await api.post(
            "/train",
            formData,
            {
                headers: {
                    "Content-Type": "multipart/form-data"
                },
                signal
            }
        );
        return response.data;
    }

    async downloadCleanedDataset(): Promise<Blob> {
    const response = await api.get(
        "/train/cleaned-dataset",
        {
            responseType: "blob"
        }
    );
    return response.data;
    }

    async predict(formData: FormData) {
    const response = await api.post(
        "/predict",
        formData,
        {
            headers: {
                "Content-Type": "multipart/form-data"
            }
        }
    );
    return response.data;
    }

    async getLatestPrediction() {
        const response = await api.get(
            "/predict/latest"
        );
        return response.data;
    }

    async getLatestModel() {
        const response = await api.get(
            "/train/latest-model"
        );
        return response.data;
    }

    async getModelComparisons() {
        const response = await api.get(
            "/train/model-comparisons"
        );
        return response.data;
    }
}

export default new AIModelService();