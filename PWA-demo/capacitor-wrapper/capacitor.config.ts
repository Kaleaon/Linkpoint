import { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.linkpoint.pwa',
  appName: 'Linkpoint',
  webDir: '..',
  server: {
    androidScheme: 'https',
    iosScheme: 'https',
    hostname: 'linkpoint.app',
    // Allow navigation to all URLs (bypasses CORS)
    allowNavigation: [
      'https://login.agni.lindenlab.com',
      'https://login.aditi.lindenlab.com',
      'http://login.osgrid.org',
      'https://*.lindenlab.com',
      'https://*.secondlife.com',
      '*'
    ]
  },
  plugins: {
    CapacitorHttp: {
      enabled: true
    },
    SplashScreen: {
      launchShowDuration: 2000,
      backgroundColor: '#1a1a1a',
      showSpinner: true,
      spinnerColor: '#4a9eff'
    }
  },
  android: {
    allowMixedContent: true,
    captureInput: true,
    webContentsDebuggingEnabled: true
  },
  ios: {
    contentInset: 'automatic',
    scrollEnabled: true
  }
};

export default config;
