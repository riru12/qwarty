import { useAuth } from ".";
import { apiService } from "@services/api/ApiService";
import type {
    Endpoint,
    EndpointReq,
    EndpointRes
} from "@services/api/endpoints/endpoint";

export const useApi = () => {
    // hook to use api service
    const { accessToken } = useAuth(); // uses hook for auth context to be able to update app-wide accessToken

    const call = async <E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>
    ): Promise<EndpointRes<E>> => {
        const response = await apiService.call(endpoint, payload, accessToken);

        return response;
    };

    return { call };
};
