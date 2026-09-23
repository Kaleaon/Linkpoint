import React, { createContext, useCallback, useContext, useMemo, useState } from 'react';
import type { SystemDialogKind } from '../data/slTypes';

/** The screens the viewer ships. Order is the order the nav offers them. */
export const SCREENS = [
  'Chat',
  'Friends',
  'Radar',
  'Map',
  'World',
  'Inventory',
  'Profile',
  'Groups',
  'Notices',
  'Teleport',
  'Settings',
  'Diagnostics',
] as const;

export type ScreenCondition = 'normal' | 'loading' | 'empty' | 'error';

export type ScreenId = (typeof SCREENS)[number];

interface ViewerContextValue {
  screen: ScreenId;
  setScreen: (s: ScreenId) => void;
  /** Forces a screen into its loading, empty or error presentation. */
  condition: ScreenCondition;
  setCondition: (c: ScreenCondition) => void;
  /** The system dialog currently raised over the viewer, if any. */
  dialog: SystemDialogKind | null;
  setDialog: (d: SystemDialogKind | null) => void;
  /** Draw distance in metres, the way the graphics preferences express it. */
  drawDistance: number;
  setDrawDistance: (m: number) => void;
  /** Preferences that several screens read. */
  prefs: ViewerPrefs;
  setPref: <K extends keyof ViewerPrefs>(key: K, value: ViewerPrefs[K]) => void;
}

export interface ViewerPrefs {
  pushNotifications: boolean;
  voiceIndicator: boolean;
  chatCommands: boolean;
  autoresponse: boolean;
  showTypingIndicator: boolean;
  /** Play a sound and vibrate on an IM. */
  imAlerts: boolean;
  /** Keep the screen awake while in the 3D view. */
  keepAwake: boolean;
  /** Stream object textures over mobile data. */
  mobileData: boolean;
}

const DEFAULT_PREFS: ViewerPrefs = {
  pushNotifications: true,
  voiceIndicator: true,
  chatCommands: true,
  autoresponse: true,
  showTypingIndicator: true,
  imAlerts: true,
  keepAwake: true,
  mobileData: false,
};

const ViewerContext = createContext<ViewerContextValue | null>(null);

export const ViewerProvider: React.FC<{ children: React.ReactNode; initialScreen?: ScreenId }> = ({
  children,
  initialScreen = 'Chat',
}) => {
  const [screen, setScreen] = useState<ScreenId>(initialScreen);
  const [condition, setCondition] = useState<ScreenCondition>('normal');
  const [dialog, setDialog] = useState<SystemDialogKind | null>(null);
  const [drawDistance, setDrawDistance] = useState(96);
  const [prefs, setPrefs] = useState<ViewerPrefs>(DEFAULT_PREFS);

  const setPref = useCallback(<K extends keyof ViewerPrefs>(key: K, value: ViewerPrefs[K]) => {
    setPrefs((p) => ({ ...p, [key]: value }));
  }, []);

  // Changing screen clears any state override, so a forced error on one screen
  // does not follow the resident around the whole viewer.
  const goto = useCallback((s: ScreenId) => {
    setScreen(s);
    setDialog(null);
  }, []);

  const value = useMemo<ViewerContextValue>(
    () => ({ screen, setScreen: goto, condition, setCondition, dialog, setDialog, drawDistance, setDrawDistance, prefs, setPref }),
    [screen, goto, condition, dialog, drawDistance, prefs, setPref],
  );

  return <ViewerContext.Provider value={value}>{children}</ViewerContext.Provider>;
};

export function useViewer(): ViewerContextValue {
  const ctx = useContext(ViewerContext);
  if (!ctx) throw new Error('useViewer must be used inside a ViewerProvider');
  return ctx;
}
