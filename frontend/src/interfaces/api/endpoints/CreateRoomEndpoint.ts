import type { Endpoint } from "../endpoint";
import type { UserType } from "./IdentityEndpoint";

export interface PlayerInfoDTO {
  username: string;
  userType: UserType;
}

export interface RoomDetailsDTO {
    roomId: string;
    gameMode: string;
    players: PlayerInfoDTO[];
}

export const CreateRoomEndpoint: Endpoint<void, RoomDetailsDTO> = {
    route: "/rooms/create",
    method: "POST",
};
