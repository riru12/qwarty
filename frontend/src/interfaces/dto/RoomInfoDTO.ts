import type { GameStatus, PlayerProgress } from "@interfaces/game";

export interface RoomInfoDTO {
    status: GameStatus;
    textPrompt: string;
    playerProgressMap: Record<string, PlayerProgress>;
}