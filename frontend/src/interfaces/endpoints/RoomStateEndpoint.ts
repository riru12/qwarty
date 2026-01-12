import type { Endpoint } from "@interfaces";
import type { GameStateDTO } from "@interfaces/dto";

export const RoomStateEndpoint: Endpoint<void, GameStateDTO> = {
    route: "/room/:roomId",
    method: "GET",
};