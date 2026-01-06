import { useCallWithGuestFallback } from "@hooks/useCallWithGuestFallback";
import { JoinRoomEndpoint } from "@interfaces/api/endpoints/JoinRoomEndpoint";
import { RoomRoute } from "@routes/routes";
import { useQuery } from "@tanstack/react-query";
import { useMatch } from "@tanstack/react-router";

export const Room = () => {
    const { params } = useMatch({ from: RoomRoute.id });
    const { roomId } = params;
    const { callWithGuestFallback } = useCallWithGuestFallback();

    const { data: roomData } = useQuery({
        queryKey: ["joinRoom", roomId],
        queryFn: async () => callWithGuestFallback(JoinRoomEndpoint, { pathParams: { roomId } }),
        enabled: !!roomId,
        staleTime: Infinity,
    });

    return (
        <div>
            {roomData?.gameMode}
            <br />
            {roomData?.players}
        </div>
    );
};
