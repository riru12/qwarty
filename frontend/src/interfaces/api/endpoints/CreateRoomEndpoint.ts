import type { Endpoint } from "../endpoint";

export interface CreateRoomResponseDTO {
    id: string;
    gameMode: string;
    roomState: string;
    players: string[];
}

export const CreateRoomEndpoint: Endpoint<void, CreateRoomResponseDTO> = {
    route: "/rooms/create",
    method: "POST",
};
