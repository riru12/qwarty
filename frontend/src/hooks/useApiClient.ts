import type { Endpoint, EndpointReq, EndpointRes } from "@interfaces";
import { GuestEndpoint } from "@interfaces/endpoints";
import { apiClient, ApiError } from "@utils/ApiClient";
import { useAuth } from "./useAuth";

export const useApiClient = () => {
    const { getAuthState, updateAuthState } = useAuth();

    const call = async <E extends Endpoint<any, any>>(
        endpoint: E, 
        options?: {
            payload?: EndpointReq<E>;
            queryParams?: Record<string, string | number | boolean>;
            pathParams?: Record<string, string | number>;
        },
    ): Promise<EndpointRes<E>> => {
        return apiClient.call(endpoint, options)
    }

    const callWithGuestFallback = async <E extends Endpoint<any, any>>(
        endpoint: E,
        options?: {
            payload?: EndpointReq<E>;
            queryParams?: Record<string, string | number | boolean>;
            pathParams?: Record<string, string | number>;
        },
    ): Promise<EndpointRes<E>> => {
        if (getAuthState().userType === "ANON") {
            try {
                await apiClient.call(GuestEndpoint);
                updateAuthState();
            } catch (guestError) {
                throw guestError;
            }
        }

        try {
            return await apiClient.call(endpoint, options);
        } catch (error) {
            if (error instanceof ApiError && error.problemDetail.status === 401 && getAuthState().userType !== "USER") {
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

    return { call, callWithGuestFallback };
}