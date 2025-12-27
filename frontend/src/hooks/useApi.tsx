import {
    GuestEndpoint,
    IdentityEndpoint,
    RefreshEndpoint
} from "@/services/api/endpoints";
import { useAuth } from ".";
import { ApiError, apiService } from "@services/api/ApiService";
import type {
    Endpoint,
    EndpointReq,
    EndpointRes
} from "@services/api/endpoints/endpoint";

interface CallOptions {
    retry?: boolean;
    fallbackToGuest?: boolean;
}

// hook to use api service
export const useApi = () => {
    const auth = useAuth(); // uses hook for auth context to retrieve and use accessToken in requests

    const call = async <E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>,
        options?: CallOptions
    ): Promise<EndpointRes<E>> => {
        const { retry = true, fallbackToGuest = true } = options ?? {};

        const payloadClone = structuredClone(payload)

        try {
            const response = await apiService.call(endpoint, payloadClone);
            return response;
        } catch (error) {
            if (!(error instanceof ApiError)) {
                throw error;
            }

            if (!retry || error.problemDetail.status !== 401) {
                throw error;
            }

            /**
             * If the error is caused by authentication issues, retry:
             *
             * 1) Call for a new accessToken through `/refresh` then reattempt original call
             * 2) If 1. fails and fallbackToGuest is enabled, proceed to 3.
             * 3) Call for a new accessToken through `/guest` then reattempt original call
             */
            try {
                await handleRefresh();
                return call(endpoint, payloadClone, {
                    ...options,
                    retry: false
                }); // disable reattempt to prevent loop
            } catch (refreshError) {
                if (!fallbackToGuest) {
                    auth.setAuthState(null);
                    throw refreshError;
                }

                await handleGuestFallback();
                return call(endpoint, payloadClone, {
                    ...options,
                    retry: false
                });
            }
        }
    };

    const handleRefresh = async () => {
        await apiService.synchronizedTask("refresh", () =>
            apiService.call(RefreshEndpoint)
        );
    };

    const handleGuestFallback = async () => {
        await apiService.synchronizedTask("guest", async () => {
            await apiService.call(GuestEndpoint);
            const identity = await apiService.call(IdentityEndpoint);
            auth.setAuthState(identity);
        });
    };

    return { call };
};
