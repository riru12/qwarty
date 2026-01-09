import type { LoginRequestDTO } from "@interfaces/dto";
import type { Endpoint } from "../Endpoint";

export const LoginEndpoint: Endpoint<LoginRequestDTO, void> = {
    route: "/public/auth/login",
    method: "POST",
};
