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
        // try {
        //     const response = await apiService.call(endpoint, payload, auth.accessToken.current);
        //     return response;
        // } catch {
        //     auth.isGuest ? await auth.guest() : await auth.refresh();
        //     const response = await apiService.call(endpoint, payload, auth.accessToken.current);
        //     return response;
        // }
        const response = await apiService.call(endpoint, payload, auth.accessToken.current);
        auth.isGuest ? await auth.guest() : await auth.refresh();
        return response;

    };

    return { call };
};
