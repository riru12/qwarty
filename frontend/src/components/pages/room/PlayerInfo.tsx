import "./PlayerInfo.css";

export const PlayerInfo = ({ username } : { username: string | null }) => {
    return (
        <div className="player-info">{username}</div>
    );
}