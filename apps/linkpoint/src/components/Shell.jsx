import NavShell from "./NavShell.jsx";
import ScreenBody from "./ScreenBody.jsx";

// Consolidated navigation shell wrapper
export default function Shell() {
  return (
    <NavShell>
      <ScreenBody />
    </NavShell>
  );
}

