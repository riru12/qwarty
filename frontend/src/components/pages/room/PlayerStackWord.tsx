import "./PlayerStackWord.css";

export const PlayerStackWord = ({ word, isActive, typed } : { word:string; isActive: boolean; typed?: string; }) => {
    const getCharClassName = (char: string, idx: number): string => {
        if (!typed || idx >= typed.length) return "";
        return typed[idx] === char ? "correct" : "incorrect";
    };

    return isActive ? (
        <div className="stack-word active-word">
            {word.split("").map((char, idx) => (
                <span key={idx} className={getCharClassName(char, idx)}>
                    {char}
                </span>
            ))}
        </div>
    ) : (
        <div className="stack-word">{word}</div>
    );
}