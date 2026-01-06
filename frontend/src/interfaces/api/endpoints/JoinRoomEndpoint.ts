import type { Endpoint } from "../endpoint";

export interface JoinRoomResponseDTO {
    id: string;
    gameMode: string;
    roomState: string;
    players: string[];
}

export const JoinRoomEndpoint: Endpoint<void, JoinRoomResponseDTO> = {
    route: "/rooms/join/:roomId",
    method: "POST",
};
