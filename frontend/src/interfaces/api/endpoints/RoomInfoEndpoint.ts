import type { Endpoint } from "../Endpoint";

export interface GameRoomDetailsDTO {
    roomId: string;
    players: string[];
}

export const RoomInfoEndpoint: Endpoint<void, GameRoomDetailsDTO> = {
    route: "/game/info/:roomId",
    method: "GET",
};
