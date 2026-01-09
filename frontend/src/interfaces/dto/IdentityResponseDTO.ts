import type { UserType } from "@interfaces/UserType";

export interface IdentityResponseDTO {
    username: string;
    userType: UserType;
}