import { useQuery } from "@tanstack/react-query";
import { useMatch } from "@tanstack/react-router";
import { useCallWithGuestFallback } from "@hooks/useCallWithGuestFallback";
import { SocketProvider } from "@contexts/SocketContext";
import { RoomRoute } from "@routes/routes";
import { RoomInfoEndpoint } from "@interfaces/api/endpoints";
import { LiveRoom } from "@components/ui";

const BASE_URL = import.meta.env.VITE_BACKEND_URL;
const WS_BASE_URL = BASE_URL.replace(/^http/, "ws");

export const Room = () => {
    const { params } = useMatch({ from: RoomRoute.id });
    const { roomId } = params;
    const { callWithGuestFallback } = useCallWithGuestFallback();

    /**
     * Upon opening the room, retrieve the room's details
     * 
     * As a side effect, the user is granted a guest token (if they do not have one)
     * due to calling with guest fallback.
     */
    const { data: roomData, isSuccess } = useQuery({
        queryKey: ["roomInfo", roomId],
        queryFn: async () => callWithGuestFallback(RoomInfoEndpoint, { pathParams: { roomId } }),
        enabled: !!roomId,
        staleTime: Infinity,
    });

    return (
        isSuccess && <SocketProvider url={`${WS_BASE_URL}/api/ws`}>
            <LiveRoom roomData={roomData} roomId={roomId} />
        </SocketProvider>
    );
}