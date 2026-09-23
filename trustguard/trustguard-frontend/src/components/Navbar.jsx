import { useNavigate } from "react-router-dom";

function Navbar()
{
    const navigate = useNavigate();

    const logout = () =>
    {
        localStorage.removeItem("token");
        navigate("/login");
    };

    return (
        <header className="topbar">

            <div
                className="topbar-brand"
                onClick={() => navigate("/analyze")}
            >
                <div className="brand-box">
                    TG
                </div>

                <div className="brand-copy">
                    <strong>
                        TRUSTGUARD
                    </strong>

                    <span>
                        THREAT INTELLIGENCE
                    </span>
                </div>
            </div>

            <div className="topbar-center">

                <span className="topbar-route">
                    OPERATIONS
                </span>

                <span className="topbar-separator">
                    /
                </span>

                <span>
                    THREAT ANALYSIS
                </span>

            </div>

            <div className="topbar-actions">

                <div className="session-status">
                    <span></span>
                    SECURE SESSION
                </div>

                <button
                    className="logout-btn"
                    onClick={logout}
                >
                    LOG OUT
                </button>

            </div>

        </header>
    );
}

export default Navbar;