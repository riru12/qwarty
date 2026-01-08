import "../styles/PlayerBar.css";

type PlayerBarProps = {
    username: string | undefined | null
    player: 1 | 2
};

export const PlayerBar = ({ username, player } : PlayerBarProps) => {
    return (
        <div className="player-bar-container">
            <div className={`player-${player}-bar`}>
                <div className="player-bar-details">
                    <h1 className={`player-${player}-bar-name ${!username ? "italic" : ""}`}>{!!username ? username : "waiting for player..."}</h1>
                </div>
            </div>
        </div>
    )
}