import type { Endpoint } from "./endpoint";

interface GuestResponseDTO {
    accessToken: string;
    username: string;
}

export const GuestEndpoint: Endpoint<void, GuestResponseDTO> = {
    route: "/auth/guest",
    method: "GET"
};
