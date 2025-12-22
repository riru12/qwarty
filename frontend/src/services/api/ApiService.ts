import type { Endpoint, EndpointReq, EndpointRes } from "./endpoints/endpoint";

const BASE_URL = import.meta.env.VITE_BACKEND_URL;

export class ApiService {
    async request<E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>
    ): Promise<EndpointRes<E>> {
        const url = new URL(`api${endpoint.route}`, BASE_URL);

        const requestInit: RequestInit = {
            method: endpoint.method,
        };

        if (payload !== undefined) {
            requestInit.headers = {
                "Content-Type": "application/json",
            }
            requestInit.body = JSON.stringify(payload);
        }

        const response = await fetch(url.toString(), requestInit);

        if (!response.ok) {
            throw new Error(`${endpoint.method} ${url}: ${response.status}`);
        }

        const parsedResponse = await response.json() as EndpointRes<E>;
        return parsedResponse;
    }
}

export default new ApiService();