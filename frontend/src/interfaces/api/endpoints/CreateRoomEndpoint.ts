import type { Endpoint } from "../Endpoint";

export interface RoomDetailsDTO {
    roomId: string;
    gameMode: string;
    players: string[];
}

export const CreateRoomEndpoint: Endpoint<void, RoomDetailsDTO> = {
    route: "/rooms/create",
    method: "POST",
};
