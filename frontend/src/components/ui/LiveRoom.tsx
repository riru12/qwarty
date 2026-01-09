import { useEffect, useState } from "react";
import { useSocket } from "@hooks/useSocket";
import type { Client, StompSubscription } from "@stomp/stompjs";
import type { GameRoomDetailsDTO } from "@interfaces/api/endpoints";
import type { GameStatus, GameState } from "@interfaces/game";
import { Stacker, WaitingScreen } from ".";

export const LiveRoom = ({ roomData, roomId }: { roomData: GameRoomDetailsDTO; roomId: string }) => {
    const { client } = useSocket();
    const [gameStatus, setGameStatus] = useState<GameStatus>("WAITING");
    const [gameState, setGameState] = useState<GameState>({
        sequence: 0,
        p1Stack: [],
        p2Stack: [],
        player1: null,
        player2: null,
        lastUpdate: new Date().toISOString(),
        lastUpdatedBy: null,
    })
    const [players, setPlayers] = useState<string[]>(roomData.players || []);

    /**
     * Subscribe to user's room error queue and handle errors received
     */
    const subscribeToErrors = (stompClient: Client): StompSubscription => {
        return stompClient.subscribe(`/user/queue/errors/${roomId}`, (message) => {
            console.error("Room Error:", message.body);
        });
    };

    /**
     * Subscribe to the room's topic and handle join and leave events received
     */
    const subscribeToRoom = (stompClient: Client): StompSubscription => {
        return stompClient.subscribe(`/topic/room/${roomId}`, (message) => {
            const event = JSON.parse(message.body);
            if (event.messageType === "JOIN" || event.messageType === "LEAVE") {
                setPlayers(event.players);
            }
            if (event.messageType === "GAME_START") {
                setGameStatus("IN_PROGRESS")
            }
            if (event.messageType === "GAME_STATE") {
                setGameState(event.stackerGameState);
            }
        });
    };

    useEffect(() => {
        if (!client || !client.connected) return;
        
        // 1. establish subscriptions
        const subErrors = subscribeToErrors(client);
        const subRoom = subscribeToRoom(client);

        // 2. announce to other players that you joined
        client.publish({
            destination: `/app/room.join/${roomId}`,
        });

        // 3. cleanup on unmount
        return () => {
            if (client.connected) { // announce to other players that you are leaving
                client.publish({ destination: `/app/room.leave/${roomId}` });
            }
            subErrors.unsubscribe();
            subRoom.unsubscribe();
        };
    }, [client, client?.connected]);

    const render = () => {
        switch(gameStatus) {
            case "WAITING":
                return (<WaitingScreen players={players} />)
            case "IN_PROGRESS":
                return (<Stacker state={gameState}/>)
            default:
                return (<div>error</div>)
        }
    }

    return (
        render()
    );
};