import type { Endpoint } from "../Endpoint";

export const LogoutEndpoint: Endpoint<void, void> = {
    route: "/public/auth/logout",
    method: "POST",
};
