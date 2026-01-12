import type { Endpoint } from "@interfaces";
import type { RoomIdDTO } from "@interfaces/dto";

export const RoomCreateEndpoint: Endpoint<void, RoomIdDTO> = {
    route: "/room",
    method: "POST",
};