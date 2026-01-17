import { useEffect, useState } from "react";
import { useAuth } from "@hooks/useAuth";
import { useSocket } from "@hooks/useSocket";
import type { GameState, GameStatus } from "@interfaces/game";
import type { RoomInfoDTO } from "@interfaces/dto";
import type { Client, StompSubscription } from "@stomp/stompjs";
// import { Stacker } from "./Stacker";
import { PlayerStack } from "./PlayerStack";

export const Stacker = ({ roomId, roomInfo }: { roomId: string, roomInfo: RoomInfoDTO }) => {
    const { client } = useSocket();
    const { getAuthState } = useAuth();
    const [ currGameStatus, setCurrGameStatus ] = useState<GameStatus>(roomInfo.status);
    const [ currGameState, setCurrGameState ] = useState<GameState>(roomInfo.state);

    /**
     * Data to identify player 1 and 2, and player and opponent stacks
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

    return (
         <div>
            <div>game status: {currGameStatus}</div>

            <div>
                <strong>{getAuthState().username}</strong>
                <PlayerStack playerStack={playerStack} />
            </div>

            <div>
                <strong>{opponentName}</strong>
                <PlayerStack playerStack={opponentStack} />
            </div>
        </div>
    )
}