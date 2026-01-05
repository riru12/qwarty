import "../styles/ModeButton.css";

type ModeButtonProps = {
    name: string;
    description: string;
    onClick?: () => void;
};

export const ModeButton = ({ name, description, onClick }: ModeButtonProps) => {
    return (
        <div className="mode-button-container">
            <div className="mode-button" onClick={onClick}>
                <div className="mode-details">
                    <h1 className="mode-title">{name}</h1>
                    <p className="mode-description">{description}</p>
                </div>
            </div>
        </div>
    );
};
