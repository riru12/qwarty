import { RoomRoute } from "@routes/routes";
import { useMatch } from "@tanstack/react-router";

export const Room = () => {
    const { params } = useMatch({ from: RoomRoute.id }); 

    return (
        <div>
            welcome to room {params.roomId}
        </div>
    )
}