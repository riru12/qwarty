import { useApiClient } from "@hooks/useApiClient";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import { RoomCreateEndpoint } from "@interfaces/endpoints";
import type { RoomIdDTO } from "@interfaces/dto";
import { ModeButton } from "./ModeButton";
import hoverSoundFile from "@assets/audio/modebuttonhover.wav";
import "./ModeSelector.css";

type Mode = {
    id: string;
    name: string;
    description: string;
};

export const ModeSelector = () => {
    const { t } = useTranslation(["global"]);
    const navigate = useNavigate();
    const { callWithGuestFallback } = useApiClient();

    const modes: Mode[] = [
        { id: "stacker", name: t("stacker"), description: t("stacker.description") }
    ];

    const hoverSound = new Audio(hoverSoundFile);

    // TODO: Add toast on error here
    const createRoomMutation = useMutation({
        mutationFn: async () => {
            const response = await callWithGuestFallback(RoomCreateEndpoint);
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
                <ModeButton key={mode.id} name={mode.name} description={mode.description} sound={hoverSound} onClick={() => createRoomMutation.mutate()} />
            ))}
        </div>
    );
};
