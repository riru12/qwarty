import type { PlayerProgress } from "@interfaces/game";

export const RacerProgress = ({
    textPrompt,
    playerName,
    playerProgress,
}: {
    textPrompt: string;
    playerName: string;
    playerProgress: PlayerProgress;
}) => {
    const promptLength = textPrompt.length;

    const progress =
        promptLength === 0
            ? 0
            : Math.min(
                  100,
                  Math.round((playerProgress.position / promptLength) * 100)
              );

    return (
        <div>
            <strong>{playerName}</strong>: {progress}%
        </div>
    );
};
