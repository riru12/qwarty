import type { Endpoint } from "../endpoint";

export const GuestEndpoint: Endpoint<void, void> = {
    route: "/public/auth/guest",
    method: "POST",
};
