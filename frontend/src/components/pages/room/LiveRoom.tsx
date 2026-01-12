import { useEffect } from "react";
import { useSocket } from "@hooks/useSocket";
import type { GameStateDTO } from "@interfaces/dto";
import type { Client, StompSubscription } from "@stomp/stompjs";


export const LiveRoom = ({ roomId, roomData }: { roomId: string, roomData: GameStateDTO }) => {
    const { client } = useSocket();

    /**
     * Subscribe to the room's topic and handle join and leave events received
     */
    const subscribeToRoom = (stompClient: Client): StompSubscription => {
        return stompClient.subscribe(`/topic/room/${roomId}`, (message) => {
            const event = JSON.parse(message.body);
            console.log(event);
        });
    };

    useEffect(() => {
        if (!client || !client.connected) return;
        
        // 1. establish subscriptions
        // const subErrors = subscribeToErrors(client);
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
            // subErrors.unsubscribe();
            subRoom.unsubscribe();
        };
    }, [client, client?.connected]);

    return (
        <div> test </div>
    )
}