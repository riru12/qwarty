import { GuestEndpoint, IdentityEndpoint, RefreshEndpoint } from "@/services/api/endpoints";
import { useAuth } from ".";
import { apiService } from "@services/api/ApiService";
import type {
    Endpoint,
    EndpointReq,
    EndpointRes
} from "@services/api/endpoints/endpoint";

interface options {
    fallbackToGuest?: boolean;
    retry?: boolean;
}

// hook to use api service
export const useApi = () => {
    const auth = useAuth(); // uses hook for auth context to retrieve and use accessToken in requests

    const call = async <E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>,
        options: options = {}
    ): Promise<EndpointRes<E>> => {
        try {
            const response = await apiService.call(endpoint, payload);
            return response;
        } catch (error) {
            if (options.retry) {
                try {
                    await apiService.synchronizedTask("refresh", () => 
                        apiService.call(RefreshEndpoint)
                    );
                    return call(endpoint, payload, { ...options, retry: false });   // disable reattempt to prevent loop
                } catch (refreshError) {
                    if (options.fallbackToGuest) {
                        await apiService.synchronizedTask("guest", async () => {
                            await apiService.call(GuestEndpoint);
                            const identity = await apiService.call(IdentityEndpoint);
                            auth.setAuthState(identity);
                        });
                        return call(endpoint, payload, { ...options, retry: false });
                    }
                    auth.setAuthState(null);
                    throw refreshError;
                }
            }
            
            throw error;
        }

    };

    return { call };
};