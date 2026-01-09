export type GameState = {
    sequence: number;
    p1Stack: string[];
    p2Stack: string[];
    player1: string | null;
    player2: string | null;
    lastUpdate: string;
    lastUpdatedBy: string | null;
};
