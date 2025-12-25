import type { Endpoint } from './endpoint';

export const GuestEndpoint: Endpoint<void, void> = {
    route: "/auth/guest",
    method: "GET"
};