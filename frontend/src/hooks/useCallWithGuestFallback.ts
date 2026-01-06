import type { Endpoint, EndpointReq, EndpointRes } from "@interfaces/api";
import { GuestEndpoint } from "@interfaces/api/endpoints";
import { apiClient } from "@utils/ApiClient";
import { useAuth } from "./useAuth";

export const useCallWithGuestFallback = () => {
    const { getAuthState, updateAuthState } = useAuth();

    const callWithGuestFallback = async <E extends Endpoint<any, any>>(
        endpoint: E,
        options?: {
            payload?: EndpointReq<E>;
            queryParams?: Record<string, string | number | boolean>;
            pathParams?: Record<string, string | number>;
        },
    ): Promise<EndpointRes<E>> => {
        try {
            return await apiClient.call(endpoint, options);
        } catch (error) {
            if (getAuthState().userType !== "USER") {
                try {
                    await apiClient.call(GuestEndpoint);
                    updateAuthState();
                    return await apiClient.call(endpoint, options);
                } catch (guestError) {
                    throw guestError;
                }
            }
            throw error;
        }
    };

    return { callWithGuestFallback };
};
