import type { Endpoint } from "./endpoint";

interface GameResponseDTO {
    message: string;
}

export const GameEndpoint: Endpoint<void, GameResponseDTO> = {
    route: "/game/create",
    method: "POST"
};
