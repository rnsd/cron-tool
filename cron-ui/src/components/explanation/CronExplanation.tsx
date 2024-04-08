import React from "react";

type CronProps = {
    scheduleExplanation: String;
};

const CronExplanation: React.FC<CronProps> = ({ scheduleExplanation }) => {
    return (
        <div
            style={{
                position: "relative",
                height: "300px",
                display: "flex",
                alignItems: "flex-end",
                justifyContent: "center",
            }}
        >
            <div
                style={{
                    fontSize: "40pt",
                    color: "grey",
                    backgroundColor: "white",
                    textAlign: "center",
                    border: "none",
                    boxShadow: "none",
                    marginTop: "20px",
                    marginBottom: "50px",
                    width: "1200px",
                    whiteSpace: "normal",
                    wordWrap: "break-word",
                    overflow: "hidden",
                    textOverflow: "ellipsis",
                }}
            >
                {scheduleExplanation ? `"${scheduleExplanation}"` : ""}
            </div>
        </div>
    );
};

export default CronExplanation;