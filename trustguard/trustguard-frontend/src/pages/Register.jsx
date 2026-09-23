import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { register } from "../services/api";

function Register()
{
    const navigate = useNavigate();

    const [username, setUsername] =
        useState("");

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");

    const [error, setError] =
        useState("");

    const [loading, setLoading] =
        useState(false);

    const handleSubmit = async (event) =>
    {
        event.preventDefault();

        setError("");

        if (
            !username.trim() ||
            !email.trim() ||
            !password
        )
        {
            setError(
                "Complete all required fields."
            );

            return;
        }

        setLoading(true);

        try
        {
            await register(
                username.trim(),
                email.trim(),
                password
            );

            navigate("/login");
        }
        catch (exception)
        {
            setError(
                exception.message ||
                "Unable to create the account."
            );
        }
        finally
        {
            setLoading(false);
        }
    };

    return (
        <div className="auth-layout">

            <section className="auth-showcase register-showcase">

                <div className="showcase-grid"></div>

                <div className="showcase-top">
                    <div className="logo-box">
                        TG
                    </div>

                    <div className="logo-text">
                        TRUSTGUARD
                    </div>
                </div>

                <div className="showcase-main">

                    <span className="showcase-eyebrow">
                        THREAT INVESTIGATION
                    </span>

                    <h1>
                        Know what's
                        <br />
                        behind the link.
                    </h1>

                    <p>
                        TrustGuard combines multiple
                        security signals to turn suspicious
                        content into an understandable threat assessment.
                    </p>

                    <div className="register-accent">
                        <span></span>
                        <span></span>
                        <span></span>
                        <span></span>
                        <span></span>
                        <span></span>
                    </div>

                </div>

                <div className="showcase-bottom">
                    <span>
                        TRUSTGUARD // OPERATOR ACCESS
                    </span>

                    <span>
                        02
                    </span>
                </div>

            </section>

            <section className="auth-form-area">

                <div className="auth-form">

                    <div className="form-kicker">
                        NEW OPERATOR
                    </div>

                    <h2>
                        Create account
                    </h2>

                    <p className="form-subtitle">
                        Set up your secure TrustGuard access.
                    </p>

                    <form onSubmit={handleSubmit}>

                        <div className="form-field">

                            <label htmlFor="username">
                                USERNAME
                            </label>

                            <input
                                id="username"
                                type="text"
                                value={username}
                                onChange={(event) =>
                                    setUsername(
                                        event.target.value
                                    )
                                }
                                placeholder="Choose a username"
                                autoComplete="username"
                            />

                        </div>

                        <div className="form-field">

                            <label htmlFor="register-email">
                                EMAIL ADDRESS
                            </label>

                            <input
                                id="register-email"
                                type="email"
                                value={email}
                                onChange={(event) =>
                                    setEmail(
                                        event.target.value
                                    )
                                }
                                placeholder="you@example.com"
                                autoComplete="email"
                            />

                        </div>

                        <div className="form-field">

                            <label htmlFor="register-password">
                                PASSWORD
                            </label>

                            <input
                                id="register-password"
                                type="password"
                                value={password}
                                onChange={(event) =>
                                    setPassword(
                                        event.target.value
                                    )
                                }
                                placeholder="Create a password"
                                autoComplete="new-password"
                            />

                        </div>

                        {error && (
                            <div className="auth-error">
                                {error}
                            </div>
                        )}

                        <button
                            className="primary-auth-button"
                            type="submit"
                            disabled={loading}
                        >
                            {loading
                                ? "CREATING ACCOUNT..."
                                : "CREATE ACCOUNT"
                            }

                            <span>
                                →
                            </span>
                        </button>

                    </form>

                    <div className="auth-divider">
                        <span></span>
                        <small>OR</small>
                        <span></span>
                    </div>

                    <p className="form-switch">
                        Already have an account?

                        <Link to="/login">
                            Sign in
                        </Link>
                    </p>

                </div>

            </section>

        </div>
    );
}

export default Register;