import { ModeButton } from "./ModeButton";
import { useTranslation } from "react-i18next";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";
import { useCallWithGuestFallback } from "@hooks/useCallWithGuestFallback";
import { CreateRoomEndpoint, type RoomIdDTO,  } from "@interfaces/api/endpoints";
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
        { id: "stacker", name: t("stacker"), description: t("stacker.description") }
    ];

    // TODO: Add Toast
    const createRoomMutation = useMutation({
        mutationFn: async (mode: string) => {
            const response = await callWithGuestFallback(CreateRoomEndpoint, { queryParams: { mode } });
            return response;
        },
        onSuccess: (response: RoomIdDTO) => {
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
