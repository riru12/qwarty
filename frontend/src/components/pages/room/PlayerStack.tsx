import { PlayerStackWord } from "./PlayerStackWord";
import "./PlayerStack.css";

export const PlayerStack = ({ playerStack, typed }: { playerStack: string[], typed?: string; }) => {
    return (
        <div className="stack-container">
            {playerStack.map((word, idx) => (
                <PlayerStackWord key={idx} word={word} isActive={idx === 0} typed={idx === 0 ? typed : ""} />
            ))}
        </div>
    );
}