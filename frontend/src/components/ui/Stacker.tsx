import { useAuth } from "@hooks/useAuth";
import { useSocket } from "@hooks/useSocket";
import type { GameState } from "@interfaces/game"
import { useEffect, useState } from "react";

export const Stacker = ({ state } : {state: GameState}) => {
    console.log(state);

    const { client } = useSocket();
    const { getAuthState } = useAuth();

    const targetText = state.p1Stack[state.p1Stack.length - 1] ?? "";
    const [typed, setTyped] = useState("");

    // Reset typed text when target changes
    useEffect(() => {
        setTyped("");
    }, [targetText]);

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (!targetText) return;

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

                    return prev.slice(0, -1);
                });
                return;
            }

            // Ignore non-character keys
            if (e.key.length !== 1) return;

            setTyped((prev) => {
                const index = prev.length;

                // Block input if last char was wrong
                if (index > 0 && prev[index - 1] !== targetText[index - 1]) {
                    return prev;
                }

                // Stop at end
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
        <div>
            <div>sequence: {state.sequence}</div>
            <div>lastUpdate: {state.lastUpdate}</div>
            <div>lastUpdatedBy: {state.lastUpdatedBy ?? "null"}</div>

            <div>{typed}</div>

            <div>{state.player1}</div>
            {state.p1Stack.map((item, idx) => (
                <div key={`p1-${idx}`}>- {item}</div>
            ))}

            <div>{state.player2}</div>
            {state.p2Stack.map((item, idx) => (
                <div key={`p2-${idx}`}>- {item}</div>
            ))}
        </div>
    )
}