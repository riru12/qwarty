import type { Endpoint } from "../endpoint";

export type UserType = "USER" | "GUEST" | "ANON";

interface IdentityResponseDTO {
    username: string;
    userType: UserType;
}

export const IdentityEndpoint: Endpoint<void, IdentityResponseDTO> = {
    route: "/auth/me",
    method: "GET",
};
