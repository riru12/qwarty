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
        { id: "racer", name: t("racer"), description: t("racer.description") },
        { id: "classic", name: t("classic"), description: t("classic.description") },
        { id: "zen", name: t("zen"), description: t("zen.description") },
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
