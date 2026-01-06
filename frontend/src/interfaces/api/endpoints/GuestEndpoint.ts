import type { Endpoint } from "../Endpoint";

export const GuestEndpoint: Endpoint<void, void> = {
    route: "/public/auth/guest",
    method: "POST",
};
