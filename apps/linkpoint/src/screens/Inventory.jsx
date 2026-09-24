import Icon from "../components/Icon.jsx";
import { useViewerState } from "../viewer/ViewerClientContext";

export default function Inventory() {
  const { connection } = useViewerState();
  return <div className="honest-empty"><Icon name="folder-open" size={28} /><p>{connection === "connected" ? "Inventory transport is waiting for the native core." : "Connect to a grid to load inventory."}</p></div>;
}
