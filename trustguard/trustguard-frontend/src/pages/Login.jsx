import { useState } from "react";
import {
    Link,
    useNavigate
} from "react-router-dom";

import { login } from "../services/api";

function Login()
{
    const navigate = useNavigate();

    const [email, setEmail] =
        useState("");

    const [password, setPassword] =
        useState("");

    const [error, setError] =
        useState("");

    const [loading, setLoading] =
        useState(false);

    const handleSubmit = async (
        event
    ) =>
    {
        event.preventDefault();

        setError("");

        if (
            !email.trim() ||
            !password
        )
        {
            setError(
                "Enter your email and password."
            );

            return;
        }

        setLoading(true);

        try
        {
            const data =
                await login(
                    email.trim(),
                    password
                );

            const token =
                data?.token ??
                data?.jwt ??
                data?.accessToken;

            if (!token)
            {
                throw new Error(
                    "Authentication succeeded but no token was returned."
                );
            }

            localStorage.setItem(
                "token",
                token
            );

            navigate("/analyze");
        }
        catch (exception)
        {
            setError(
                exception.message ||
                "Unable to authenticate."
            );
        }
        finally
        {
            setLoading(false);
        }
    };

    return (
        <div className="auth-page">

            <section className="auth-visual">

                <div className="auth-grid"></div>

                <div className="auth-brand">
                    <div className="auth-brand-box">
                        TG
                    </div>

                    <div>
                        <strong>
                            TRUSTGUARD
                        </strong>

                        <span>
                            CYBER THREAT INTELLIGENCE
                        </span>
                    </div>
                </div>

                <div className="auth-hero">

                    <span className="auth-eyebrow">
                        THREAT INVESTIGATION PLATFORM
                    </span>

                    <h1>
                        Investigate
                        <br />
                        before you trust.
                    </h1>

                    <p>
                        Analyze suspicious messages and
                        links through AI classification,
                        security rules, URL analysis and
                        threat intelligence.
                    </p>

                    <div className="auth-system-panel">

                        <div className="auth-system-header">

                            <span>
                                TRUSTGUARD ENGINE
                            </span>

                            <span className="auth-live">
                                ONLINE
                            </span>

                        </div>

                        <div className="auth-engine-grid">

                            <div>
                                <span>01</span>
                                <strong>
                                    AI CLASSIFICATION
                                </strong>
                            </div>

                            <div>
                                <span>02</span>
                                <strong>
                                    URL INTELLIGENCE
                                </strong>
                            </div>

                            <div>
                                <span>03</span>
                                <strong>
                                    RISK ASSESSMENT
                                </strong>
                            </div>

                        </div>

                    </div>

                </div>

                <div className="auth-footer">

                    <span>
                        TRUSTGUARD // SECURE ANALYSIS PLATFORM
                    </span>

                    <span>
                        01
                    </span>

                </div>

            </section>

            <section className="auth-form-area">

                <div className="auth-form-wrap">

                    <div className="auth-form-eyebrow">
                        SECURE ACCESS
                    </div>

                    <h2>
                        Welcome back
                    </h2>

                    <p className="auth-form-description">
                        Sign in to access the threat
                        investigation console.
                    </p>

                    <form
                        onSubmit={handleSubmit}
                    >

                        <div className="form-group">

                            <label htmlFor="login-email">
                                EMAIL ADDRESS
                            </label>

                            <input
                                id="login-email"
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

                        <div className="form-group">

                            <label htmlFor="login-password">
                                PASSWORD
                            </label>

                            <input
                                id="login-password"
                                type="password"
                                value={password}
                                onChange={(event) =>
                                    setPassword(
                                        event.target.value
                                    )
                                }
                                placeholder="Enter your password"
                                autoComplete="current-password"
                            />

                        </div>

                        {error && (
                            <div className="auth-error">
                                {error}
                            </div>
                        )}

                        <button
                            className="auth-submit"
                            type="submit"
                            disabled={loading}
                        >
                            <span>
                                {loading
                                    ? "AUTHENTICATING..."
                                    : "AUTHENTICATE"
                                }
                            </span>

                            <span className="submit-arrow">
                                →
                            </span>
                        </button>

                    </form>

                    <div className="auth-or">
                        <span></span>
                        <small>OR</small>
                        <span></span>
                    </div>

                    <p className="auth-switch">
                        Don't have an account?

                        <Link to="/register">
                            Create account
                        </Link>
                    </p>

                    <div className="auth-note">
                        <span></span>
                        JWT protected application access
                    </div>

                </div>

            </section>

        </div>
    );
}

export default Login;