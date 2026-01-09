import type { SignupRequestDTO } from "@interfaces/dto";
import type { Endpoint } from "../Endpoint";

export const SignupEndpoint: Endpoint<SignupRequestDTO, void> = {
    route: "/public/auth/signup",
    method: "POST",
};
