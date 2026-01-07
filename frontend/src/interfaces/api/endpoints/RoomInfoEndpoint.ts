import type { Endpoint } from "../Endpoint";
import type { RoomDetailsDTO } from "./CreateRoomEndpoint";

export const RoomInfoEndpoint: Endpoint<void, RoomDetailsDTO> = {
    route: "/rooms/info/:roomId",
    method: "GET",
};
