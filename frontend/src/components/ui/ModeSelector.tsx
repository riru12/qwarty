import { ModeButton } from "./ModeButton";
import "../styles/ModeSelector.css";

type Mode = {
    id: string;
    name: string;
    description: string;
};

const modes: Mode[] = [
    { id: "racer", name: "racer", description: "Short description..." },
    { id: "classic", name: "classic", description: "Short description..." },
    { id: "zen", name: "zen", description: "Short description..." },
];

export const ModeSelector = () => {

    return (
        <div className="mode-selector">
            {modes.map((mode: Mode) => (
                <ModeButton
                    key={mode.id}
                    name={mode.name}
                    description={mode.description}
                    onClick={() => console.log(`Selected mode: ${mode.id}`)}
                />
            ))}
        </div>
    );
}