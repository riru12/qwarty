import type { Endpoint } from "../Endpoint";

export interface GameRoomIdDTO {
    roomId: string;
}

export const CreateRoomEndpoint: Endpoint<void, GameRoomIdDTO> = {
    route: "/game/create",
    method: "POST",
};
