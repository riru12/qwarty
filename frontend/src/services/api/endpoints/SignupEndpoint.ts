import type { Endpoint } from './endpoint';

interface SignupRequestDTO {
    email: string,
    username: string,
    password: string
}

export const SignupEndpoint: Endpoint<SignupRequestDTO, void> = {
    route: "/auth/signup",
    method: "POST"
};