import type { Word } from "./Word";

export interface GameState {
    player1: string | null;
    player2: string | null;
    player1Stack: Word[];
    player2Stack: Word[];
}
