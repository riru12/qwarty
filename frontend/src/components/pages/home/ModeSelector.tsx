import { ModeButton } from "./ModeButton";
import { useTranslation } from "react-i18next";
import hoverSoundFile from "@assets/audio/modebuttonhover.wav";
import "./ModeSelector.css";

type Mode = {
    id: string;
    name: string;
    description: string;
};

export const ModeSelector = () => {
    const { t } = useTranslation(["global"]);

    const modes: Mode[] = [
        { id: "stacker", name: t("stacker"), description: t("stacker.description") }
    ];

    const hoverSound = new Audio(hoverSoundFile);

    return (
        <div className="mode-selector">
            {modes.map((mode: Mode) => (
                <ModeButton key={mode.id} name={mode.name} description={mode.description} sound={hoverSound} onClick={() => console.log(`Selected mode: ${mode.id}`)} />
            ))}
        </div>
    );
};
