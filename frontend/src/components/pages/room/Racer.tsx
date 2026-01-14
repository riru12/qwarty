import { useEffect, useState } from "react";
import { RacerProgress } from "./RacerProgress"
import type { GameState, GameStatus } from "@interfaces/game"

export const Racer = ({ gameStatus, gameState }: { gameStatus: GameStatus, gameState: GameState }) => {
    const targetText = gameState.textPrompt;
    const [typed, setTyped] = useState("");

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            // Always allow backspace (with your rules)
            if (e.key === "Backspace") {
                setTyped((prev) => {
                    if (prev.length === 0) return prev;

                    const lastIndex = prev.length - 1;
                    const lastTypedChar = prev[lastIndex];
                    const correctChar = targetText[lastIndex];

                    // Block backspace if last char is correct
                    if (lastTypedChar === correctChar) {
                        return prev;
                    }

                    // Allow backspace only if last char is wrong
                    return prev.slice(0, -1);
                });
                return;
            }

            // Ignore non-character keys
            if (e.key.length !== 1) return;

            setTyped((prev) => {
                const index = prev.length;

                // If last character was wrong, block input
                if (index > 0 && prev[index - 1] !== targetText[index - 1]) {
                    return prev;
                }

                // Stop at end of text
                if (index >= targetText.length) {
                    return prev;
                }

                return prev + e.key;
            });
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [targetText]);

    return (
        <>  
            <div>Status: {gameStatus}</div>
            <p>{gameState.textPrompt}</p>
            <p>{typed}</p>
            <RacerProgress textPrompt={gameState.textPrompt} playerProgress={gameState.playerProgress} />
        </>
    )
}