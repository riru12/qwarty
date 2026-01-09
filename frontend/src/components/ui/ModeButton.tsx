import { useRef } from "react";
import "@components/styles/ModeButton.css";

type ModeButtonProps = {
    name: string;
    description: string;
    sound: HTMLAudioElement;
    onClick?: () => void;
};

let lastSoundPlay = 0;

export const ModeButton = ({ name, description, sound, onClick }: ModeButtonProps) => {
    const hoverSound = useRef(sound);
    hoverSound.current.volume = 0.025;
    const playSound = () => {
        const now = Date.now();

        if (now - lastSoundPlay > 100) {
            lastSoundPlay = now;
            hoverSound.current.currentTime = 0;
            hoverSound.current.play().catch(() => {});
        }
    };

    return (
        <div className="mode-button-container">
            <div className="mode-button" onClick={onClick} onMouseEnter={playSound}>
                <div className="mode-details">
                    <h1 className="mode-title">{name}</h1>
                    <p className="mode-description">{description}</p>
                </div>
            </div>
        </div>
    );
};
