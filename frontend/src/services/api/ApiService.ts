import type { Endpoint, EndpointReq, EndpointRes } from "./endpoints/endpoint";
import { GuestEndpoint, LoginEndpoint, RefreshEndpoint } from "./endpoints";

const BASE_URL = import.meta.env.VITE_BACKEND_URL;

export class ApiService {
    private accessToken: string | null = null;

    private setAccessToken(token: string) {
        this.accessToken = token;
    }
    
    /**
     * Perform a fetch to the provided endpoint, with one re-attempt if initial call fails.
     * 
     * @param endpoint 
     * @param payload 
     * @param retry 
     * @returns 
     */
    public async call<E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>,
        retry: boolean = false
    ): Promise<EndpointRes<E>> {
        
        const url = new URL(`api${endpoint.route}`, BASE_URL);
        const request = this.buildRequest(endpoint, payload);

        let response = await fetch(url.toString(), request);

        if (!response.ok) {
            if (!retry) { // check if we can attempt a retry
                return await this.retryCall(endpoint, payload);
            } else { 
                throw new Error(`Request failed with status ${response.status}`);
            }
        }

        const parsedResponse = await response.json() as EndpointRes<E>;
        this.updateAccessToken(endpoint, parsedResponse);
        return parsedResponse;
    }

    /**
     * Retry API call by attempting to update refresh token
     * 
     * If refreshing token fails, let the user continue as a guest by requesting a guest JWT
     * 
     * @param endpoint 
     * @param payload 
     * @returns 
     */
    private async retryCall<E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>,
    ): Promise<EndpointRes<E>> {

        try {
            // attempt to request for new accessToken using refreshToken (for signed in users)
            await this.call(RefreshEndpoint, undefined, true);
        } catch (refreshError) {
            // if refresh fails, let the user continue as guest
            console.warn("Refresh token failed, proceeding as Guest.", refreshError);
            try {
                await this.call(GuestEndpoint, undefined, true);
            } catch (guestError) {
                console.error("Guest token request failed.", guestError);
                throw guestError;
            }
        }

        return await this.call(endpoint, payload, true);   // attempt the request again
    }

    /**
     * Build the request with headers, payload, method, etc.
     * 
     * @param endpoint 
     * @param payload 
     * @returns request object
     */
    private buildRequest<E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>
    ): RequestInit {
        const headers: Record<string, string> = {};

        if (this.accessToken) {
            headers["Authorization"] = `Bearer ${this.accessToken}`;
        }

        if (payload !== undefined) {
            headers["Content-Type"] = "application/json";
        }

        return {
            method: endpoint.method,
            credentials: 'include',
            headers,
            body: payload !== undefined ? JSON.stringify(payload) : undefined
        };
    }

    /**
     * Updates the stored access token if the response from Login, Refresh, or Guest
     * contains one, so future API calls use the new token.
     * 
     * @param endpoint 
     * @param parsedResponse 
     * @returns 
     */
    private updateAccessToken<E extends Endpoint<any, any>>(
        endpoint: E,
        parsedResponse: EndpointRes<E>
    ) {
        if (endpoint !== LoginEndpoint && endpoint !== RefreshEndpoint && endpoint !== GuestEndpoint) {
            return;
        }
        if ("accessToken" in parsedResponse && typeof parsedResponse.accessToken === "string") {
            this.setAccessToken(parsedResponse.accessToken);
        }
    }
}

export default new ApiService();