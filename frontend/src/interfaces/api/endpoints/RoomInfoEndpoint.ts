import type { Endpoint } from "../Endpoint";

export interface RoomDetailsDTO {
    roomId: string;
    players: string[];
}

export const RoomInfoEndpoint: Endpoint<void, RoomDetailsDTO> = {
    route: "/rooms/info/:roomId",
    method: "GET",
};
