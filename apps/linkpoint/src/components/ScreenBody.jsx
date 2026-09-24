import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import { lazy, Suspense } from "react";
import { useViewerClient, useViewerState } from "../viewer/ViewerClientContext";
import Header from "./Header.jsx";
import Icon from "./Icon.jsx";
import { LAYOUTS } from "../theme/layouts.js";
import { PALETTES } from "../theme/palettes.js";
const Chat = lazy(() => import("../screens/Chat.jsx"));
const Inventory = lazy(() => import("../screens/Inventory.jsx"));
const World3D = lazy(() => import("../screens/World3D.jsx"));
const Login = lazy(() => import("../screens/Login.jsx"));
const live = import("../screens/LiveScreens.jsx");
const tools = import("../screens/LumiyaTools.jsx");
const FriendsScreen = lazy(() => live.then((module) => ({ default: module.FriendsScreen })));
const GenericInventoryScreen = lazy(() => live.then((module) => ({ default: module.GenericInventoryScreen })));
const GroupsScreen = lazy(() => live.then((module) => ({ default: module.GroupsScreen })));
const MapScreen = lazy(() => live.then((module) => ({ default: module.MapScreen })));
const MuteListScreen = lazy(() => live.then((module) => ({ default: module.MuteListScreen })));
const NoticesScreen = lazy(() => live.then((module) => ({ default: module.NoticesScreen })));
const RadarScreen = lazy(() => live.then((module) => ({ default: module.RadarScreen })));
const SearchScreen = lazy(() => live.then((module) => ({ default: module.SearchScreen })));
const AccountsScreen = lazy(() => tools.then((module) => ({ default: module.AccountsScreen })));
const DiagnosticsScreen = lazy(() => tools.then((module) => ({ default: module.DiagnosticsScreen })));
const GridsScreen = lazy(() => tools.then((module) => ({ default: module.GridsScreen })));
const MediaScreen = lazy(() => tools.then((module) => ({ default: module.MediaScreen })));
const NotecardsScreen = lazy(() => tools.then((module) => ({ default: module.NotecardsScreen })));
const ParcelScreen = lazy(() => tools.then((module) => ({ default: module.ParcelScreen })));
const TeleportScreen = lazy(() => tools.then((module) => ({ default: module.TeleportScreen })));
const TransactionsScreen = lazy(() => tools.then((module) => ({ default: module.TransactionsScreen })));

export default function ScreenBody() {
  const { state } = useApp();
  const { scr } = useTheme();
  return (
    <div style={{ flex: 1, minWidth: 0, display: "flex", flexDirection: "column" }}>
      {scr !== "Login" ? <Header /> : null}
      <Suspense fallback={<div className="honest-empty">Loading screen…</div>}>
      {scr === "Login" ? <Login /> : null}
      {scr === "Chat" ? <Chat /> : null}
      {scr === "Inventory" ? <Inventory /> : null}
      {scr === "Friends" ? <FriendsScreen /> : null}
      {scr === "Radar" ? <RadarScreen /> : null}
      {scr === "Map" ? <MapScreen /> : null}
      {scr === "3D View" ? <World3D /> : null}
      {scr === "Groups" ? <GroupsScreen /> : null}
      {scr === "Notices" ? <NoticesScreen /> : null}
      {scr === "Mute List" ? <MuteListScreen /> : null}
      {scr === "Outfits" ? <GenericInventoryScreen kind="wearable" /> : null}
      {scr === "Objects" ? <GenericInventoryScreen kind="object" /> : null}
      {scr === "Teleport" ? <TeleportScreen /> : null}
      {scr === "Parcel" ? <ParcelScreen /> : null}
      {scr === "Transactions" ? <TransactionsScreen /> : null}
      {scr === "Notecards" ? <NotecardsScreen /> : null}
      {scr === "Media" ? <MediaScreen /> : null}
      {scr === "Accounts" ? <AccountsScreen /> : null}
      {scr === "Grids" ? <GridsScreen /> : null}
      {scr === "Diagnostics" ? <DiagnosticsScreen /> : null}
      {scr === "Profile" ? <Profile /> : null}
      {scr === "Settings" ? <Settings /> : null}
      {scr === "Search" ? <SearchScreen /> : null}
      </Suspense>
    </div>
  );
}

function Profile() {
  const { session } = useViewerState();
  return session ? (
    <div className="honest-empty"><Icon name="user" size={34} /><h2>{session.agentId}</h2><p>{session.regionName}</p></div>
  ) : <Unavailable icon="user" title="Profile" message="Connect to a grid to view your profile." />;
}

function Settings() {
  const { state, actions } = useApp();
  const client = useViewerClient();
  const viewer = useViewerState();
  const notificationsEnabled = state.toggles?.push !== false;
  const logout = async () => {
    if (viewer.connection !== "disconnected") await client.execute({ type: "session.logout" });
    actions.setScreen("Login");
  };
  return (
    <div className="settings-screen">
      <section><h2>Layout</h2><div className="choice-grid">{Object.entries(LAYOUTS).map(([key, item]) => <button className={state.layout === key ? "selected" : ""} key={key} onClick={() => actions.setLayout(key)}>{item.name}</button>)}</div></section>
      <section><h2>Colour</h2><div className="choice-grid">{Object.entries(PALETTES).map(([key, item]) => <button className={state.palette === key ? "selected" : ""} key={key} onClick={() => actions.setPalette(key)}>{item.name}</button>)}</div></section>
      <section><h2>Notifications</h2><label className="setting-toggle"><input type="checkbox" defaultChecked={notificationsEnabled} onChange={() => actions.toggleSetting("push")} /> Enable viewer notifications</label></section>
      <section><button className="danger-action" onClick={() => void logout()}>{viewer.connection === "connected" ? "Log out" : "Return to login"}</button></section>
    </div>
  );
}

function Unavailable({ icon, title, message }) {
  return <div className="honest-empty"><Icon name={icon} size={30} /><h2>{title}</h2><p>{message}</p></div>;
}
