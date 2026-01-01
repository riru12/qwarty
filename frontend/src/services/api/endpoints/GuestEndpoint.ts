import type { Endpoint } from "./endpoint";

interface GuestResponseDTO {
    accessToken: string;
}

export const GuestEndpoint: Endpoint<void, GuestResponseDTO> = {
    route: "/auth/guest",
    method: "GET"
};
