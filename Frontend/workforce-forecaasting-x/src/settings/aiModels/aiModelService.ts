import axios from "axios";
import type { TrainingResponseDTO } from "./trainingDTO";

class AIModelService {

    async train(formData: FormData, signal?: AbortSignal): Promise<TrainingResponseDTO> {
        const response = await axios.post(
            "http://localhost:5233/api/train",
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
    const response = await axios.get(
        "http://localhost:5233/api/train/cleaned-dataset",
        {
            responseType: "blob"
        }
    );
    return response.data;
    }

    async predict(formData: FormData) {
    const response = await axios.post(
        "http://localhost:5233/api/predict",
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
        const response = await axios.get(
            "http://localhost:5233/api/predict/latest"
        );
        return response.data;
    }

    async getLatestModel() {
        const response = await axios.get(
            "http://localhost:5233/api/train/latest-model"
        );
        return response.data;
    }

    async getModelComparisons() {
        const response = await axios.get(
            "http://localhost:5233/api/train/model-comparisons"
        );
        return response.data;
    }
}

export default new AIModelService();