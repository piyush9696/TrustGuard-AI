import { useLocation, useNavigate } from "react-router-dom";

function Sidebar()
{
    const location = useLocation();
    const navigate = useNavigate();

    const modules = [
        "AI CLASSIFICATION",
        "RULE ANALYSIS",
        "URL INTELLIGENCE",
        "WEB RISK",
        "RISK ASSESSMENT"
    ];

    return (
        <aside className="sidebar">

            <div className="sidebar-top">

                <div className="sidebar-heading">
                    OPERATIONS
                </div>

                <button
                    className={
                        location.pathname === "/analyze"
                            ? "sidebar-link active"
                            : "sidebar-link"
                    }
                    onClick={() =>
                        navigate("/analyze")
                    }
                >
                    <span className="sidebar-link-icon">
                        ◈
                    </span>

                    <span>
                        Threat Analysis
                    </span>

                    <span className="sidebar-arrow">
                        →
                    </span>
                </button>

            </div>

            <div className="sidebar-section">

                <div className="sidebar-heading">
                    ANALYSIS MODULES
                </div>

                <div className="module-list">

                    {modules.map(
                        (module) => (
                            <div
                                className="module-row"
                                key={module}
                            >
                                <span className="module-status"></span>
                                {module}
                            </div>
                        )
                    )}

                </div>

            </div>

            <div className="sidebar-divider"></div>

            <div className="sidebar-info">

                <div className="sidebar-info-label">
                    PIPELINE
                </div>

                <div className="sidebar-info-title">
                    MULTI-SIGNAL
                </div>

                <p>
                    AI interpretation combined with
                    deterministic security signals.
                </p>

            </div>

            <div className="sidebar-bottom">

                <span className="online-dot"></span>

                <div>
                    <strong>
                        SYSTEM ONLINE
                    </strong>

                    <small>
                        TRUSTGUARD CORE
                    </small>
                </div>

            </div>

        </aside>
    );
}

export default Sidebar;