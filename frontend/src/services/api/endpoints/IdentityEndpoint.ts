import type { Endpoint } from "./endpoint";

interface IdentityResponseDTO {
    username: string;
    isGuest: boolean;
}

export const IdentityEndpoint: Endpoint<void, IdentityResponseDTO> = {
    route: "/me",
    method: "GET"
};
