import { useQuery } from "@tanstack/react-query";
import { useMatch } from "@tanstack/react-router";
import { useApiClient } from "@hooks/useApiClient";
import { SocketProvider } from "@contexts/SocketContext";
import { RoomRoute } from "@routes/routes";
import { RoomStateEndpoint } from "@interfaces/endpoints";

const BASE_URL = import.meta.env.VITE_BACKEND_URL;
const WS_BASE_URL = BASE_URL.replace(/^http/, "ws");

export const Room = () => {
    const { params } = useMatch({ from: RoomRoute.id });
    const { roomId } = params;
    const { callWithGuestFallback } = useApiClient();

    /**
     * Upon opening the room, retrieve the room's state
     * 
     * As a side effect, the user is granted a guest token (if they do not have one)
     * due to calling with guest fallback.
     */
    const { data: roomData, isSuccess } = useQuery({
        queryKey: ["roomState", roomId],
        queryFn: async () => callWithGuestFallback(RoomStateEndpoint, { pathParams: { roomId } }),
        enabled: !!roomId,
        staleTime: Infinity,
    });

    if (!isSuccess) return <div>Loading room...</div>;

    return (
        <SocketProvider url={`${WS_BASE_URL}/api/ws`}>
            <div>{roomData.state.textPrompt}</div>
            <div>{roomId}</div>
        </SocketProvider>
    );
}