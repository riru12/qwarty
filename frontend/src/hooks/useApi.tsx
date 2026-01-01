import { useAuth } from ".";
import { apiService } from "@services/api/ApiService";
import type {
    Endpoint,
    EndpointReq,
    EndpointRes
} from "@services/api/endpoints/endpoint";

// hook to use api service
export const useApi = () => {
    const auth = useAuth(); // uses hook for auth context to retrieve and use accessToken in requests

    const call = async <E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>
    ): Promise<EndpointRes<E>> => {
        const response = await apiService.call(endpoint, payload, auth.accessToken.current);

        return response;
    };

    return { call };
};
