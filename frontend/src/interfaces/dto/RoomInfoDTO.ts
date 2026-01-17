import type { GameState, GameStatus } from "@interfaces/game";

export interface RoomInfoDTO {
    status: GameStatus;
    state: GameState;
}