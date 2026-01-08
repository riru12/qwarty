import { useAuth } from "@hooks/useAuth";
import { PlayerBar } from "./PlayerBar";
import "../styles/WaitingScreen.css";

type WaitingScreenProps = {
    players: string[];
};

export const WaitingScreen = ({ players } : WaitingScreenProps) => {
    const { getAuthState } = useAuth();

    const player1 = getAuthState().username;
    const player2 = players.find(p => p !== player1);

    return (
        <div className="waiting-screen-container">
            <div className="player-1-bar-container">
                <PlayerBar username={player1} player={1}/>
            </div>
            <div className="player-2-bar-container">
                <PlayerBar username={player2} player={2}/>
            </div>
        </div>
    )
}