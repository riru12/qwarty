import type { Endpoint } from "./endpoint";

export const LogoutEndpoint: Endpoint<void, void> = {
    route: "/auth/session/logout",
    method: "POST"
};
