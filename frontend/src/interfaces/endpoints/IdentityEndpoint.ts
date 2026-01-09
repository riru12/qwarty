
import type { IdentityResponseDTO } from "@interfaces/dto";
import type { Endpoint } from "../Endpoint";

export const IdentityEndpoint: Endpoint<void, IdentityResponseDTO> = {
    route: "/public/auth/me",
    method: "GET",
};
