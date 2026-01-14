export const RacerProgress = ({ textPrompt, playerProgress }: { textPrompt: string; playerProgress: Record<string, string>; }) => {
    const promptLength = textPrompt.length;
    return (
        <div>
        {Object.entries(playerProgress).map(([playerName, typedText]) => {
            const progress =
            promptLength === 0 ? 0 : Math.min( 100, Math.round((typedText.length / promptLength) * 100));
            return (
            <div key={playerName}>
                <strong>{playerName}</strong>: {progress}%
            </div>
            );
        })}
        </div>
    );
};