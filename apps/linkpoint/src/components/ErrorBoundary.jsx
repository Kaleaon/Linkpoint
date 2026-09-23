import { Component } from "react";

// A screen that throws should not take the whole viewer down with it: the
// device frame, the pickers and every other screen stay usable, and the
// failure is reported in place with a way back.
export default class ErrorBoundary extends Component {
  constructor(props) {
    super(props);
    this.state = { error: null };
  }

  static getDerivedStateFromError(error) {
    return { error };
  }

  componentDidCatch(error, info) {
    console.error("[Linkpoint] render error in <" + (this.props.label || "app") + ">", error, info);
  }

  // Remounting the subtree is what clears the error; the key bump on the
  // wrapper is what callers use to retry after changing state.
  reset = () => this.setState({ error: null });

  render() {
    const { error } = this.state;
    if (!error) return this.props.children;

    return (
      <div
        role="alert"
        style={{
          flex: 1,
          minHeight: 0,
          display: "flex",
          flexDirection: "column",
          gap: "10px",
          justifyContent: "center",
          alignItems: "center",
          padding: "24px",
          textAlign: "center",
          font: '400 12px/1.6 "JetBrains Mono", ui-monospace, monospace',
          color: "#e8e5df",
          background: "#100f0e",
        }}
      >
        <div style={{ font: '600 12px/1 "JetBrains Mono", monospace', letterSpacing: ".18em", color: "#ff8f7a" }}>
          {(this.props.label || "SCREEN").toUpperCase()} FAILED TO RENDER
        </div>
        <div style={{ color: "rgba(255,255,255,.55)", maxWidth: "360px", wordBreak: "break-word" }}>{String(error && error.message ? error.message : error)}</div>
        <button type="button" className="tag" onClick={this.reset}>
          RETRY
        </button>
      </div>
    );
  }
}
