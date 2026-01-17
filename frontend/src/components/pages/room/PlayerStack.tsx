import { PlayerStackWord } from "./PlayerStackWord";

export const PlayerStack = ({ playerStack }: { playerStack: string[] }) => {
    return (
        <div>
            {playerStack.map((word, idx) => (
                <PlayerStackWord key={idx} word={word} />
            ))}
        </div>
    );
}