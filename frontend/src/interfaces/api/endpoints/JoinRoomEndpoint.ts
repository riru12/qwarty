import type { Endpoint } from "../endpoint";
import type { RoomDetailsDTO } from "./CreateRoomEndpoint";

export const JoinRoomEndpoint: Endpoint<void, RoomDetailsDTO> = {
    route: "/rooms/join/:roomId",
    method: "POST",
};
