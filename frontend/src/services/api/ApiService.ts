import type { Endpoint, EndpointReq, EndpointRes } from "./endpoints/endpoint";

const BASE_URL = import.meta.env.VITE_BACKEND_URL;

class ApiService {
    /**
     * Perform a fetch to the provided endpoint
     */
    public async call<E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>,
        accessToken?: string | null,
    ): Promise<EndpointRes<E>> {
        
        const url = new URL(`api/${endpoint.route}`, BASE_URL);
        const request = this.buildRequest(endpoint, accessToken, payload);

        let response = await fetch(url, request);

        if (!response.ok) {
            throw new Error(`Request failed with status ${response.status}`);
        }

        const parsedResponse = await response.json() as EndpointRes<E>;

        return parsedResponse;
    }

    /**
     * Build the request with headers, payload, method, etc.
     */
    private buildRequest<E extends Endpoint<any, any>>(
        endpoint: E,
        accessToken?: string | null,
        payload?: EndpointReq<E>
    ): RequestInit {
        const headers: Record<string, string> = {};

        if (accessToken && accessToken !== '') {
            headers["Authorization"] = `Bearer ${accessToken}`;
        }

        if (payload !== undefined) {
            headers["Content-Type"] = "application/json";
        }

        return {
            method: endpoint.method,
            credentials: 'include',
            headers,
            body: JSON.stringify(payload)
        };
    }
}

export const apiService = new ApiService();