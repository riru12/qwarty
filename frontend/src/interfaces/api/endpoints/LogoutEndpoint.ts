import type { Endpoint } from "../endpoint";

export const LogoutEndpoint: Endpoint<void, void> = {
    route: "/public/auth/logout",
    method: "POST",
};
