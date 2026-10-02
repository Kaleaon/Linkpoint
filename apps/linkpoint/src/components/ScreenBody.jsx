import { useApp } from "../context/AppContext.jsx";
import { useTheme } from "../context/ThemeContext.jsx";
import Header from "./Header.jsx";
import SegmentedTabs from "./SegmentedTabs.jsx";
import ChipRow from "./ChipRow.jsx";
import StateBlock from "./StateBlock.jsx";
import SplitDetail from "./SplitDetail.jsx";
import Chat from "../screens/Chat.jsx";
import Radar from "../screens/Radar.jsx";
import Map from "../screens/Map.jsx";
import World3D, { World3DActionBar } from "../screens/World3D.jsx";
import Inventory from "../screens/Inventory.jsx";
import Profile from "../screens/Profile.jsx";
import Login from "../screens/Login.jsx";
import CacheScreen from "../screens/CacheScreen.jsx";
import Settings from "../screens/Settings.jsx";
import ContactsScreen from "../screens/ContactsScreen.jsx";
import CalendarScreen from "../screens/CalendarScreen.jsx";
import OutfitViewer from "../screens/OutfitViewer.jsx";
import { FriendsScreen, GroupsScreen, NoticesScreen, MuteListScreen, GenericInventoryScreen, SearchScreen } from "../screens/LiveScreens.jsx";
import { AOScreen, AccountsScreen, DiagnosticsScreen, GridsScreen, MediaScreen, NotecardsScreen, ParcelScreen, TeleportScreen, TransactionsScreen } from "../screens/LumiyaTools.jsx";

// Ported from the big content column inside `shellStyle` (headers -> segTabs
// -> chips -> the 13 screens' bodies), plus the split-view detail pane that
// sits beside it on tablet/foldable devices.
export default function ScreenBody() {
  const { state, actions } = useApp();
  const { norm, scr, consoleScene } = useTheme();

  return (
    <div style={{ flex: 1, minWidth: 0, display: "flex", position: "relative" }}>
      <div style={{ flex: 1, minWidth: 0, display: "flex", flexDirection: "column" }}>
        {scr !== "Login" && <Header />}
        <SegmentedTabs />
        <ChipRow />
        {norm && scr === "Chat" && <Chat />}
        {norm && scr === "Radar" && <Radar />}
        {norm && scr === "Map" && <Map />}
        {norm && scr === "3D View" && !consoleScene && (
          <>
            <World3D />
            <World3DActionBar />
          </>
        )}
        {norm && scr === "Inventory" && <Inventory />}
        {norm && scr === "Profile" && <Profile />}
        {norm && scr === "Cache" && <CacheScreen />}
        {norm && scr === "Settings" && <Settings />}
        {norm && scr === "Friends" && <FriendsScreen />}
        {norm && scr === "Contacts" && <ContactsScreen />}
        {norm && scr === "Calendar" && <CalendarScreen />}
        {norm && scr === "Groups" && <GroupsScreen />}
        {norm && scr === "Notices" && <NoticesScreen />}
        {norm && scr === "Mute List" && <MuteListScreen />}
        {norm && scr === "Outfits" && <OutfitViewer />}
        {norm && scr === "Objects" && <GenericInventoryScreen kind="object" />}
        {norm && scr === "Notecards" && <NotecardsScreen />}
        {norm && scr === "Media" && <MediaScreen />}
        {norm && scr === "Accounts" && <AccountsScreen />}
        {norm && scr === "Grids" && <GridsScreen />}
        {norm && scr === "Teleport" && <TeleportScreen />}
        {norm && scr === "Parcel" && <ParcelScreen />}
        {norm && scr === "Transactions" && <TransactionsScreen />}
        {norm && scr === "Diagnostics" && <DiagnosticsScreen />}
        {norm && scr === "AO" && <AOScreen />}
        {scr === "Login" && <Login />}
        {scr === "Search" && <SearchScreen />}
        {!norm && <StateBlock />}
      </div>
      <SplitDetail />
    </div>
  );
}
