import { useEffect, useState } from "react";
import { useAuth } from "@hooks/useAuth";
import { useSocket } from "@hooks/useSocket";
import type { GameState, GameStatus } from "@interfaces/game";
import type { RoomInfoDTO } from "@interfaces/dto";
import type { Client, StompSubscription } from "@stomp/stompjs";
import { PlayerStack } from "./PlayerStack";
import { PlayerInfo } from "./PlayerInfo";
import "./Stacker.css";

export const Stacker = ({ roomId, roomInfo }: { roomId: string, roomInfo: RoomInfoDTO }) => {
    const { client } = useSocket();
    const { getAuthState } = useAuth();
    const [ currGameStatus, setCurrGameStatus ] = useState<GameStatus>(roomInfo.status);
    const [ currGameState, setCurrGameState ] = useState<GameState>(roomInfo.state);
    const [ typedWord, setTypedWord ] = useState("");

    /**
     * Identify player and stacks
     */
    const playerName = getAuthState().username;
    const isPlayer1 = currGameState.player1 === playerName;
    const opponentName = isPlayer1 ? currGameState.player2 : currGameState.player1;
    const playerStack = isPlayer1 ? currGameState.player1Stack : currGameState.player2Stack;
    const opponentStack = isPlayer1 ? currGameState.player2Stack : currGameState.player1Stack;

    /**
     * Subscribe to user-specific messages
     */
    const subscribeToUser = (stompClient: Client): StompSubscription => {
        return stompClient.subscribe(`/user/queue/room/${roomId}`, (message) => {
            console.error("Room Error:", message.body);
        });
    };

    /**
     * Subscribe to the room's topic to receive room-wide messages
     */
    const subscribeToRoom = (stompClient: Client): StompSubscription => {
        return stompClient.subscribe(`/topic/room/${roomId}`, (message) => {
            const event = JSON.parse(message.body);
            console.log(event.payload);
            switch (event.messageType) {
                case "GAME_STATE":
                    setCurrGameState(event.payload);
                    break;
                case "GAME_STATUS":
                    setCurrGameStatus(event.payload);
                    break;
                default:
                    break;
            }
        });
    };

    useEffect(() => {
        if (!client || !client.connected) return;
        
        // 1. establish subscriptions
        const subUser = subscribeToUser(client);
        const subRoom = subscribeToRoom(client);

        // 2. announce to other players that you joined
        client.publish({
            destination: `/app/game.join/${roomId}`,
        });

        // 3. cleanup on unmount
        return () => {
            if (client.connected) { // announce to other players that you are leaving
                client.publish({ destination: `/app/game.leave/${roomId}` });
            }
            subUser.unsubscribe();
            subRoom.unsubscribe();
        };
    }, [client, client?.connected]);

    const sendWord = () => {
        if (!client || !client.connected) return;

        const trimmed = typedWord.trim();
        if (!trimmed) return;

        client.publish({
            destination: `/app/game.input/${roomId}`,
            body: JSON.stringify({ word: trimmed }),    // TODO: change this to a DTO interface
        });

        setTypedWord(""); // clear after send
    };

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            // Ignore if user is typing in an actual input/textarea
            if (e.target instanceof HTMLInputElement || e.target instanceof HTMLTextAreaElement) {
                return;
            }

            // Handle Enter to submit
            if (e.key === "Enter") {
                e.preventDefault();
                sendWord();
                return;
            }

            // Handle Backspace
            if (e.key === "Backspace") {
                e.preventDefault();
                setTypedWord(prev => prev.slice(0, -1));
                return;
            }

            // Handle regular character input
            if (e.key.length === 1) {
                // Only accept letters/alphanumeric
                if (/^[a-zA-Z]$/.test(e.key)) {
                    setTypedWord(prev => prev + e.key);
                }
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => window.removeEventListener("keydown", handleKeyDown);
    }, [typedWord]);

    return (
         <div className="play-area-container">
            <div>game status: {currGameStatus}</div>

            <div className="play-area">
                <PlayerStack playerStack={playerStack} typed={typedWord} />
                <PlayerInfo username={playerName} />
            </div>

            <div className="play-area">
                <PlayerStack playerStack={opponentStack} />
                <PlayerInfo username={opponentName} />
            </div>
        </div>
    )
}