import { useEffect, useState } from "react";
import "../styles/Racer.css";

export const Racer = () => {
    const [targetText, setTargetText] = useState("The quick brown fox jumps over the lazy dog");
    const [typed, setTyped] = useState("");

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            // Always allow backspace
            if (e.key === "Backspace") {
                setTyped((prev) => prev.slice(0, -1));
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
    }, []);

    return (
        <>
            <div>racer</div>
            <div>
                {targetText.split("").map((char, i) => {
                    let className = ""; // not typed yet

                    if (i < typed.length) {
                        className = typed[i] === char ? "correct" : "wrong";
                    }

                    return (
                        <span key={i} className={className}>
                            {char}
                        </span>
                    );
                })}
            </div>
            {/* <div>{typed}</div> */}
        </>
    );
};
