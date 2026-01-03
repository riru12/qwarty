import type { Endpoint } from "./endpoint";

export const RefreshEndpoint: Endpoint<void, void> = {
    route: "/auth/session/refresh",
    method: "POST"
};
