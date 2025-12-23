import type { Endpoint } from './endpoint';

interface LoginRequestDTO {
    username: string,
    password: string
}

interface LoginResponseDTO {
    accessToken: string
}

export const LoginEndpoint: Endpoint<LoginRequestDTO, LoginResponseDTO> = {
    route: "/auth/login",
    method: "POST"
};