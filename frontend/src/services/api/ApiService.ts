import type { Endpoint, EndpointReq, EndpointRes } from "./endpoints/endpoint";

const BASE_URL = import.meta.env.VITE_BACKEND_URL;

export interface ProblemDetail {
    status: number;
    title: string;
    detail?: string;
    errors?: Array<Record<string, string>>;
    [key: string]: any;
}

export class ApiError extends Error {
    public problemDetail: ProblemDetail;

    constructor(problemDetail: ProblemDetail) {
        super(problemDetail.title);
        this.name = "ApiError";
        this.problemDetail = problemDetail;
        Object.setPrototypeOf(this, ApiError.prototype);
    }
}

class ApiService {
    private activeTasks = new Map<string, Promise<any>>();

    /**
     * Perform a fetch to the provided endpoint
     */
    public async call<E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>
    ): Promise<EndpointRes<E>> {
        // regex ensures the endpoint route works correctly whether or not it starts with a `/`
        const url = new URL(
            `api/${endpoint.route.replace(/^\/+/, "")}`,
            BASE_URL
        );
        const request = this.buildRequest(endpoint, payload);

        let response = await fetch(url, request);

        let parsedResponse: EndpointRes<E> | undefined;
        const responseText = await response.text();
        parsedResponse = responseText ? JSON.parse(responseText) : undefined;

        if (!response.ok) {
            const problemDetail: ProblemDetail = parsedResponse || {
                status: response.status,
                title: `Request failed with status ${response.status}`
            };
            throw new ApiError(problemDetail);
        }

        return parsedResponse as EndpointRes<E>;
    }

    /**
     * Build the request with headers, payload, method, etc.
     */
    private buildRequest<E extends Endpoint<any, any>>(
        endpoint: E,
        payload?: EndpointReq<E>
    ): RequestInit {
        const headers: Record<string, string> = {};

        if (payload !== undefined) {
            headers["Content-Type"] = "application/json";
        }

        return {
            method: endpoint.method,
            credentials: "include",
            headers,
            body: JSON.stringify(payload)
        };
    }

    /**
     * Ensures that only one async task per key runs at a time.
     *
     * If a task with the same key is already in progress, subsequent callers
     * will wait for and receive the result of the existing task instead of
     * starting a new one. Once the task settles, the lock is released.
     *
     * Useful for deduplicating shared work such as token refreshes or
     * idempotent network requests.
     */
    public async synchronizedTask<T>(
        key: string,
        task: () => Promise<T>
    ): Promise<T> {
        if (this.activeTasks.has(key)) {
            return this.activeTasks.get(key)!;
        }

        const promise = task().finally(() => {
            this.activeTasks.delete(key);
        });

        this.activeTasks.set(key, promise);
        return promise;
    }
}

export const apiService = new ApiService();
