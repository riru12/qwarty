import type { Endpoint } from './endpoint';

interface SignUpRequestDTO {
    email: string,
    username: string,
    password: string
}

export const SignUpEndpoint: Endpoint<SignUpRequestDTO, void> = {
    route: "/auth/signup",
    method: "POST"
};