import { useEffect, useState } from "react";
import { useSocket } from "@hooks/useSocket";
import type { GameStatus, PlayerProgress } from "@interfaces/game";
import type { RoomInfoDTO } from "@interfaces/dto";
import type { Client, StompSubscription } from "@stomp/stompjs";
import { Racer } from "./Racer";

export const LiveRoom = ({ roomId, roomInfo }: { roomId: string, roomInfo: RoomInfoDTO }) => {
    const { client } = useSocket();
    const [ currGameStatus, setCurrGameStatus ] = useState<GameStatus>(roomInfo.status);
    const [ currPlayerProgressMap, setCurrPlayerProgressMap ] = useState<Record<string, PlayerProgress>>(roomInfo.playerProgressMap);
    const [ textPrompt ] = useState<string>(roomInfo.textPrompt);

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
                case "PLAYER_PROGRESS_MAP_STATE":
                    setCurrPlayerProgressMap(event.payload);
                    break;
                default:
                    console.warn("Message type not recognized");    // TODO: change i18n
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
        <Racer gameStatus={currGameStatus} textPrompt={textPrompt} playerProgressMap={currPlayerProgressMap} />
    )
}