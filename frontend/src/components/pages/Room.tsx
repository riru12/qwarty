import { useAuth } from "@hooks/useAuth";
import { GuestEndpoint } from "@interfaces/api/endpoints/GuestEndpoint";
import { JoinRoomEndpoint } from "@interfaces/api/endpoints/JoinRoomEndpoint";
import { RoomRoute } from "@routes/routes";
import { useQuery } from "@tanstack/react-query";
import { useMatch } from "@tanstack/react-router";
import { apiClient } from "@utils/ApiClient";

export const Room = () => {
    const { params } = useMatch({ from: RoomRoute.id }); 
    const { roomId } = params;
    const { getAuthState, updateAuthState } = useAuth();

    const { data : roomData } = useQuery({
        queryKey: ['joinRoom', roomId],
        queryFn: async () => {
            try {
                return await apiClient.call(JoinRoomEndpoint, {pathParams: {roomId}});
            } catch(error) {
                if (getAuthState().userType != "USER") {
                    try {
                        await apiClient.call(GuestEndpoint);
                        updateAuthState();
                        return await apiClient.call(JoinRoomEndpoint, {pathParams: {roomId}});
                    }
                    catch(guestError) {
                        throw guestError;
                    }

                }
                throw error;
            }
        },
        enabled: !!roomId,
        staleTime: Infinity
    })


    return (
        <div>
            {roomData?.gameMode}
            <br />
            {roomData?.players}
        </div>
    );
};