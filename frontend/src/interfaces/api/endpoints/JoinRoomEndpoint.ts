import type { Endpoint } from "../Endpoint";
import type { RoomDetailsDTO } from "./CreateRoomEndpoint";

export const JoinRoomEndpoint: Endpoint<void, RoomDetailsDTO> = {
    route: "/rooms/join/:roomId",
    method: "POST",
};
