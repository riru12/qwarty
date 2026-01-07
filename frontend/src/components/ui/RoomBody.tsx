import { useEffect, useState } from "react";
import { useSocket } from "@hooks/useSocket";
import type { Client, StompSubscription } from "@stomp/stompjs";
import type { RoomDetailsDTO } from "@interfaces/api/endpoints";
import { Racer } from "@components/modes";

export const RoomBody = ({ roomData, roomId }: { roomData: RoomDetailsDTO; roomId: string }) => {
    const client = useSocket();
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

    const renderGameMode = () => {
        if (!client) return null;

        switch (roomData?.gameMode) {
            case "RACER":
                return <Racer />;
            default:
                return <div>Unknown game mode</div>;
        }
    };

    return (
        <div>
            <h1>Mode: {roomData.gameMode}</h1>
            <div>
                Players:
                <ul>{players.map(p => <li key={p}>{p}</li>)}</ul>
            </div>

            {renderGameMode()}
        </div>
    );
};