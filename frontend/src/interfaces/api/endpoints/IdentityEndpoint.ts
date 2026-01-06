import type { Endpoint } from "../Endpoint";

export type UserType = "USER" | "GUEST" | "ANON";

interface IdentityResponseDTO {
    username: string;
    userType: UserType;
}

export const IdentityEndpoint: Endpoint<void, IdentityResponseDTO> = {
    route: "/public/auth/me",
    method: "GET",
};
