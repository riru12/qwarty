import type { Endpoint } from "@interfaces";
import type { RoomInfoDTO } from "@interfaces/dto";

export const RoomStateEndpoint: Endpoint<void, RoomInfoDTO> = {
    route: "/room/:roomId",
    method: "GET",
};