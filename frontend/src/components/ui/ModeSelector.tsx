import { ModeButton } from "./ModeButton";
import { useTranslation } from "react-i18next";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";
import { useCallWithGuestFallback } from "@hooks/useCallWithGuestFallback";
import { CreateRoomEndpoint, type RoomDetailsDTO } from "@interfaces/api/endpoints";
import "../styles/ModeSelector.css";

type Mode = {
    id: string;
    name: string;
    description: string;
};

export const ModeSelector = () => {
    const { t } = useTranslation(["global"]);
    const navigate = useNavigate();
    const { callWithGuestFallback } = useCallWithGuestFallback();
    const modes: Mode[] = [
        // { id: "racer", name: t("racer"), description: t("racer.description") },
        { id: "classic", name: t("classic"), description: t("classic.description") },
        // { id: "zen", name: t("zen"), description: t("zen.description") },
    ];

    // TODO: Add Toast
    const createRoomMutation = useMutation({
        mutationFn: async (mode: string) => {
            const response = await callWithGuestFallback(CreateRoomEndpoint, { queryParams: { mode } });
            return response;
        },
        onSuccess: (response: RoomDetailsDTO) => {
            navigate({ to: "/room/" + response.roomId });
        },
        onError: (error: any) => {
            console.error("Failed to create room:", error);
        },
    });

    return (
        <div className="mode-selector">
            {modes.map((mode: Mode) => (
                <ModeButton key={mode.id} name={mode.name} description={mode.description} onClick={() => createRoomMutation.mutate(mode.id)} />
            ))}
        </div>
    );
};
