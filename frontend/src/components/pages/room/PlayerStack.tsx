import { PlayerStackWord } from "./PlayerStackWord";
import type { Word } from "@interfaces/game";
import "./PlayerStack.css";

export const PlayerStack = ({ playerStack, typed }: { playerStack: Word[], typed?: string; }) => {
    return (
        <div className="stack-container">
            {playerStack.map((word, idx) => (
                <PlayerStackWord key={idx} word={word.text} isActive={idx === 0} typed={typed} />
            ))}
        </div>
    );
}