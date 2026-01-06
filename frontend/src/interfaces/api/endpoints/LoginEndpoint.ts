import type { Endpoint } from "../Endpoint";

interface LoginRequestDTO {
    username: string;
    password: string;
}

export const LoginEndpoint: Endpoint<LoginRequestDTO, void> = {
    route: "/public/auth/login",
    method: "POST",
};
