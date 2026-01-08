import type { Endpoint } from "../Endpoint";

export interface RoomIdDTO {
    roomId: string;
}

export const CreateRoomEndpoint: Endpoint<void, RoomIdDTO> = {
    route: "/rooms/create",
    method: "POST",
};
