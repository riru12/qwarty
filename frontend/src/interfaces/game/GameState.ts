export interface GameState {
    readonly textPrompt: string;
    playerProgress: Record<string, string>;
}