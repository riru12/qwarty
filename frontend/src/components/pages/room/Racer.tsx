import { RacerProgress } from "./RacerProgress"
import type { GameState } from "@interfaces/game"

export const Racer = ({ gameState }: { gameState: GameState }) => {
    return (
        <>
            <p>{gameState.textPrompt}</p>
            <RacerProgress textPrompt={gameState.textPrompt} playerProgress={gameState.playerProgress} />
        </>
    )
}