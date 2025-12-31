import type { Endpoint } from "./endpoint";

interface RefreshResponseDTO {
    accessToken: string;
}

export const RefreshEndpoint: Endpoint<void, RefreshResponseDTO> = {
    route: "/auth/session/refresh",
    method: "GET"
};
