import type { Endpoint } from "../Endpoint";

interface SignupRequestDTO {
    email: string;
    username: string;
    password: string;
}

export const SignupEndpoint: Endpoint<SignupRequestDTO, void> = {
    route: "/public/auth/signup",
    method: "POST",
};
