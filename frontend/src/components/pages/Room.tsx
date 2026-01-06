import { useCallWithGuestFallback } from "@hooks/useCallWithGuestFallback";
import { JoinRoomEndpoint } from "@interfaces/api/endpoints/JoinRoomEndpoint";
import { RoomRoute } from "@routes/routes";
import { useQuery } from "@tanstack/react-query";
import { useMatch } from "@tanstack/react-router";
import { Client } from "@stomp/stompjs";
import { useEffect, useRef, useState } from "react";
import { Racer } from "@components/modes";

export const Room = () => {
    const { params } = useMatch({ from: RoomRoute.id });
    const { roomId } = params;
    const { callWithGuestFallback } = useCallWithGuestFallback();
    const clientRef = useRef<Client | null>(null);
    const [players, setPlayers] = useState<string[]>([]);

    /**
     * Upon opening the link to a room, call for /rooms/join/{roomId}
     *
     * Call is done with guest fallback, so non-users can join by first getting
     * a guest session (if they already do not have one)
     */
    const { data: roomData, isSuccess } = useQuery({
        queryKey: ["joinRoom", roomId],
        queryFn: async () => callWithGuestFallback(JoinRoomEndpoint, { pathParams: { roomId } }),
        enabled: !!roomId, // Only perform a call if a roomId is provided in the URL
        staleTime: Infinity,
    });

    /**
     * Subscribe to the room's topic and handle join and leave events received
     */
    const subscribeToRoom = (client: Client) => {
        return client.subscribe(`/topic/room/${roomId}`, (message) => {
            const event = JSON.parse(message.body);

            if (event.messageType === "JOIN" || event.messageType === "LEAVE") {
                setPlayers(event.players); // replace the current players list
            }
        });
    };

    /**
     * Connect to the WebSocket and subscribe the the room's topic
     *
     * Triggered upon successful joining of room
     */
    useEffect(() => {
        // If a room wasn't successfully joined through the HTTP call, do not proceed with WS connection
        if (!roomId || !isSuccess) return;

        const client = new Client({
            brokerURL: "ws://localhost:8081/api/ws",
            onConnect: () => {
                subscribeToRoom(client); // subscribe to the room topic
                client.publish({
                    // announce to other users that you joined
                    destination: `/app/room.join/${roomId}`,
                });
            },
        });

        client.activate();
        clientRef.current = client;

        return () => {
            if (clientRef.current && clientRef.current.connected) {
                // announce to other users that you are leaving
                clientRef.current.publish({
                    destination: `/app/room.leave/${roomId}`,
                });
            }

            client.deactivate();
        };
    }, [isSuccess]);

    /**
     * Once room has been joined, update the player list state and re-render
     */
    useEffect(() => {
        if (roomData?.players) {
            setPlayers(roomData.players);
        }
    }, [roomData]);

    const renderGameMode = () => {
        if (!clientRef.current) return null;

        switch (roomData?.gameMode) {
            case "RACER":
                return <Racer />;
            default:
                return <div>Unknown game mode</div>;
        }
    };

    return (
        <div>
            {roomData?.gameMode}
            <br />
            <div>
                Players:
                <ul>
                    {players?.map((player) => (
                        <li key={player}>{player}</li>
                    ))}
                </ul>
            </div>

            {renderGameMode()}
        </div>
    );
};
