import { useState } from "react";

import Navbar from "../components/Navbar";
import Sidebar from "../components/Sidebar";

import { analyze } from "../services/api";

function Analyze()
{
    const [text, setText] =
        useState("");

    const [result, setResult] =
        useState(null);

    const [loading, setLoading] =
        useState(false);

    const [error, setError] =
        useState("");

    const handleAnalyze = async () =>
    {
        if (!text.trim())
        {
            setError(
                "Enter a message or URL before starting the analysis."
            );

            return;
        }

        setLoading(true);
        setError("");
        setResult(null);

        try
        {
            const data =
                await analyze(text.trim());

            setResult(data);
        }
        catch (exception)
        {
            if (exception.status === 401)
            {
                localStorage.removeItem("token");

                window.location.href =
                    "/login";

                return;
            }

            if (exception.status === 429)
            {
                setError(
                    exception.retryAfter
                        ? `Rate limit exceeded. Try again in ${exception.retryAfter} seconds.`
                        : "Rate limit exceeded. Please try again later."
                );

                return;
            }

            setError(
                exception.message ||
                "Unable to complete the analysis."
            );
        }
        finally
        {
            setLoading(false);
        }
    };

    const riskLevel =
        result
            ? result.riskScore >= 70
                ? "HIGH"
                : result.riskScore >= 40
                    ? "MEDIUM"
                    : "LOW"
            : "";

    const riskClass =
        riskLevel.toLowerCase();

    return (
        <div className="app-shell">

            <Navbar />

            <div className="app-body">

                <Sidebar />

                <main className="dashboard">

                    {/* HEADER */}

                    <section className="dashboard-header">

                        <div>

                            <div className="dashboard-eyebrow">
                                THREAT INVESTIGATION
                            </div>

                            <h1>
                                Analysis Console
                            </h1>

                            <p>
                                Investigate suspicious messages
                                and links using TrustGuard's
                                multi-signal security pipeline.
                            </p>

                        </div>

                        <div className="dashboard-status">
                            <span></span>
                            SYSTEM OPERATIONAL
                        </div>

                    </section>

                    {/* INVESTIGATION TARGET */}

                    <section className="target-panel">

                        <div className="target-header">

                            <div className="target-title-group">

                                <span className="panel-number">
                                    01
                                </span>

                                <div>

                                    <h2>
                                        Investigation Target
                                    </h2>

                                    <p>
                                        Paste a suspicious
                                        message or URL.
                                    </p>

                                </div>

                            </div>

                            <span className="auto-detect">
                                AUTO DETECTION
                            </span>

                        </div>

                        <textarea
                            value={text}
                            maxLength={5000}
                            onChange={(event) =>
                                setText(
                                    event.target.value
                                )
                            }
                            placeholder="Paste a suspicious message or URL..."
                        />

                        <div className="target-bottom">

                            <div className="target-meta">

                                <span>
                                    INPUT
                                </span>

                                <strong>
                                    MESSAGE / URL
                                </strong>

                                <small>
                                    {text.length} / 5000
                                </small>

                            </div>

                            <button
                                className="start-button"
                                disabled={
                                    loading ||
                                    !text.trim()
                                }
                                onClick={handleAnalyze}
                            >
                                <span>
                                    {loading
                                        ? "ANALYZING..."
                                        : "START ANALYSIS"
                                    }
                                </span>

                                <span>
                                    →
                                </span>
                            </button>

                        </div>

                        {error && (
                            <div className="analysis-error">

                                <strong>
                                    !
                                </strong>

                                <span>
                                    {error}
                                </span>

                            </div>
                        )}

                    </section>

                    {/* STANDBY / PROCESSING */}

                    {!result && !loading && (
                        <section className="overview-grid">

                            <div className="threat-map-card">

                                <div className="overview-heading">

                                    <span>
                                        THREAT SURFACE
                                    </span>

                                    <small>
                                        STANDBY
                                    </small>

                                </div>

                                <div className="threat-map">

                                    <div className="map-grid"></div>

                                    <div className="map-ring ring-large"></div>

                                    <div className="map-ring ring-medium"></div>

                                    <div className="map-ring ring-small"></div>

                                    <div className="map-cross horizontal"></div>

                                    <div className="map-cross vertical"></div>

                                    <div className="map-sweep"></div>

                                    <div className="map-core">
                                        TG
                                    </div>

                                </div>

                                <div className="map-footer">

                                    <span>
                                        NO ACTIVE TARGET
                                    </span>

                                    <span>
                                        READY
                                    </span>

                                </div>

                            </div>

                            <div className="pipeline-card">

                                <div className="overview-heading">

                                    <span>
                                        ANALYSIS PIPELINE
                                    </span>

                                    <small>
                                        MULTI-SIGNAL
                                    </small>

                                </div>

                                <div className="pipeline-list">

                                    <div className="pipeline-entry">

                                        <div className="pipeline-index">
                                            01
                                        </div>

                                        <div>

                                            <small>
                                                INTELLIGENCE
                                            </small>

                                            <strong>
                                                AI CLASSIFICATION
                                            </strong>

                                        </div>

                                        <span className="ready-chip">
                                            READY
                                        </span>

                                    </div>

                                    <div className="pipeline-entry">

                                        <div className="pipeline-index">
                                            02
                                        </div>

                                        <div>

                                            <small>
                                                DETECTION
                                            </small>

                                            <strong>
                                                RULE ANALYSIS
                                            </strong>

                                        </div>

                                        <span className="ready-chip">
                                            READY
                                        </span>

                                    </div>

                                    <div className="pipeline-entry">

                                        <div className="pipeline-index">
                                            03
                                        </div>

                                        <div>

                                            <small>
                                                URL
                                            </small>

                                            <strong>
                                                THREAT INTELLIGENCE
                                            </strong>

                                        </div>

                                        <span className="ready-chip">
                                            READY
                                        </span>

                                    </div>

                                    <div className="pipeline-entry">

                                        <div className="pipeline-index">
                                            04
                                        </div>

                                        <div>

                                            <small>
                                                DECISION
                                            </small>

                                            <strong>
                                                RISK ASSESSMENT
                                            </strong>

                                        </div>

                                        <span className="ready-chip">
                                            READY
                                        </span>

                                    </div>

                                </div>

                            </div>

                        </section>
                    )}

                    {loading && (
                        <section className="overview-grid">

                            <div className="threat-map-card scan-active">

                                <div className="overview-heading">

                                    <span>
                                        LIVE THREAT SURFACE
                                    </span>

                                    <small>
                                        PROCESSING
                                    </small>

                                </div>

                                <div className="threat-map">

                                    <div className="map-grid"></div>

                                    <div className="map-ring ring-large"></div>

                                    <div className="map-ring ring-medium"></div>

                                    <div className="map-ring ring-small"></div>

                                    <div className="map-cross horizontal"></div>

                                    <div className="map-cross vertical"></div>

                                    <div className="map-sweep"></div>

                                    <div className="scan-pulse"></div>

                                    <div className="map-core">
                                        SCAN
                                    </div>

                                </div>

                                <div className="map-footer">

                                    <span>
                                        INVESTIGATION ACTIVE
                                    </span>

                                    <span className="processing">
                                        PROCESSING
                                    </span>

                                </div>

                            </div>

                            <div className="pipeline-card">

                                <div className="overview-heading">

                                    <span>
                                        ANALYSIS PIPELINE
                                    </span>

                                    <small>
                                        RUNNING
                                    </small>

                                </div>

                                <div className="pipeline-list">

                                    <div className="pipeline-entry running">

                                        <div className="pipeline-index">
                                            01
                                        </div>

                                        <div>

                                            <small>
                                                INTELLIGENCE
                                            </small>

                                            <strong>
                                                AI CLASSIFICATION
                                            </strong>

                                        </div>

                                        <span>
                                            RUNNING
                                        </span>

                                    </div>

                                    <div className="pipeline-entry running">

                                        <div className="pipeline-index">
                                            02
                                        </div>

                                        <div>

                                            <small>
                                                DETECTION
                                            </small>

                                            <strong>
                                                RULE ANALYSIS
                                            </strong>

                                        </div>

                                        <span>
                                            RUNNING
                                        </span>

                                    </div>

                                    <div className="pipeline-entry running">

                                        <div className="pipeline-index">
                                            03
                                        </div>

                                        <div>

                                            <small>
                                                URL
                                            </small>

                                            <strong>
                                                THREAT INTELLIGENCE
                                            </strong>

                                        </div>

                                        <span>
                                            RUNNING
                                        </span>

                                    </div>

                                    <div className="pipeline-entry running">

                                        <div className="pipeline-index">
                                            04
                                        </div>

                                        <div>

                                            <small>
                                                DECISION
                                            </small>

                                            <strong>
                                                RISK ASSESSMENT
                                            </strong>

                                        </div>

                                        <span>
                                            RUNNING
                                        </span>

                                    </div>

                                </div>

                            </div>

                        </section>
                    )}

                    {/* RESULT */}

                    {result && !loading && (
                        <section className="result-panel">

                            <div className="result-panel-header">

                                <div>

                                    <div className="dashboard-eyebrow">
                                        INVESTIGATION COMPLETE
                                    </div>

                                    <h2>
                                        Threat Assessment
                                    </h2>

                                </div>

                                <span className="complete-badge">
                                    ANALYSIS COMPLETE
                                </span>

                            </div>

                            <div className="result-summary-grid">

                                <div
                                    className={
                                        result.threatDetected
                                            ? "summary-card danger-summary"
                                            : "summary-card safe-summary"
                                    }
                                >

                                    <span>
                                        THREAT STATUS
                                    </span>

                                    <strong>
                                        {result.threatDetected
                                            ? "THREAT DETECTED"
                                            : "NO THREAT DETECTED"
                                        }
                                    </strong>

                                    <small>
                                        TrustGuard assessment
                                    </small>

                                </div>

                                <div
                                    className={
                                        `summary-card score-summary ${riskClass}`
                                    }
                                >

                                    <span>
                                        RISK SCORE
                                    </span>

                                    <div className="score-value">

                                        {result.riskScore}

                                        <small>
                                            /100
                                        </small>

                                    </div>

                                    <em>
                                        {riskLevel} RISK
                                    </em>

                                </div>

                                <div className="summary-card category-summary">

                                    <span>
                                        CLASSIFICATION
                                    </span>

                                    <strong>
                                        {result.category}
                                    </strong>

                                    <small>
                                        AI confidence{" "}
                                        {Math.round(
                                            result.confidence * 100
                                        )}%
                                    </small>

                                </div>

                            </div>

                            <div className="result-layout">

                                <div className="result-main-column">

                                    <div className="result-block">

                                        <div className="result-block-heading">

                                            <span>
                                                03
                                            </span>

                                            <h3>
                                                Detection Indicators
                                            </h3>

                                        </div>

                                        {result.redFlags?.length > 0 ? (
                                            <div className="flags">

                                                {result.redFlags.map(
                                                    (
                                                        flag,
                                                        index
                                                    ) => (
                                                        <div
                                                            className="flag"
                                                            key={index}
                                                        >
                                                            <span>
                                                                !
                                                            </span>

                                                            <p>
                                                                {flag}
                                                            </p>

                                                        </div>
                                                    )
                                                )}

                                            </div>
                                        ) : (
                                            <p className="result-muted">
                                                No suspicious indicators
                                                were identified.
                                            </p>
                                        )}

                                    </div>

                                    <div className="result-block">

                                        <div className="result-block-heading">

                                            <span>
                                                04
                                            </span>

                                            <h3>
                                                Analyst Assessment
                                            </h3>

                                        </div>

                                        <p className="result-copy">
                                            {result.explanation}
                                        </p>

                                    </div>

                                </div>

                                <aside className="recommendation-card">

                                    <div className="result-block-heading">

                                        <span>
                                            05
                                        </span>

                                        <h3>
                                            Recommended Action
                                        </h3>

                                    </div>

                                    <div className="recommendation-line"></div>

                                    <p>
                                        {result.recommendation}
                                    </p>

                                </aside>

                            </div>

                            <button
                                className="new-investigation"
                                onClick={() =>
                                {
                                    setText("");
                                    setResult(null);
                                    setError("");
                                }}
                            >
                                <span>
                                    NEW INVESTIGATION
                                </span>

                                <span>
                                    →
                                </span>

                            </button>

                        </section>
                    )}

                </main>

            </div>

        </div>
    );
}

export default Analyze;