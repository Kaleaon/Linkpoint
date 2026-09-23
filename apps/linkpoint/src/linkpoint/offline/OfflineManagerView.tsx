import React, { useState, useEffect } from 'react';
import { LocalGridManager } from './LocalGridManager';
import { LocalAssetManager, AssetType } from './LocalAssetManager';
import { CacheManager } from './CacheManager';
import { OARParser } from './OARParser';
import { GridConsole, gridConsole as sharedGridConsole, LOG_COMPONENTS } from './GridConsole';
import { GridConsolePanel } from './GridConsolePanel';

interface Props {
  gridManager: LocalGridManager;
  assetManager: LocalAssetManager;
  cacheManager: CacheManager;
  /** Defaults to the shared console the offline services write to. */
  console?: GridConsole;
}

export const MIN_PASSWORD_LENGTH = 6;

export const OfflineManagerView: React.FC<Props> = ({
  gridManager,
  assetManager,
  cacheManager,
  console: consoleProp
}) => {
  const gridLog = consoleProp ?? sharedGridConsole;

  const [isRunning, setIsRunning] = useState(gridManager.isGridRunning());
  const [needsAccountSetup, setNeedsAccountSetup] = useState(gridManager.isFirstTimeSetupNeeded());

  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('Resident');
  const [password, setPassword] = useState('');
  const [isSavingAccount, setIsSavingAccount] = useState(false);
  const [accountError, setAccountError] = useState('');

  const [assetName, setAssetName] = useState('');
  const [assetType, setAssetType] = useState<AssetType>('texture');
  const [assetData, setAssetData] = useState('');
  const [assetsList, setAssetsList] = useState(assetManager.getAllAssets());

  const [cacheSettings, setCacheSettings] = useState(cacheManager.getSettings());
  const [cacheMB, setCacheMB] = useState(Math.round(cacheSettings.maxSizeBytes / (1024 * 1024)));

  const [oarXmlInput, setOarXmlInput] = useState('');
  const [importedStatus, setImportedStatus] = useState('');

  useEffect(() => {
    const handleState = (data: { isRunning: boolean }) => setIsRunning(data.isRunning);
    gridManager.on('gridStateChanged', handleState);
    return () => gridManager.off('gridStateChanged', handleState);
  }, [gridManager]);

  // Route uncaught viewer errors into the same log as grid events.
  useEffect(() => gridLog.attachGlobalErrorHandlers(), [gridLog]);

  const handleToggleState = () => {
    if (needsAccountSetup) {
      alert('Please configure your first-time account before starting the local grid.');
      return;
    }
    try {
      const nextState = !isRunning;
      gridManager.toggleGridState(nextState);
      setIsRunning(gridManager.isGridRunning());
    } catch (e: any) {
      gridLog.captureError(LOG_COMPONENTS.GRID, 'Could not change the local grid state.', e);
      alert(e?.message || 'Error toggling grid state');
    }
  };

  const handleAccountSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setAccountError('');

    if (!firstName.trim() || !password) {
      setAccountError('First Name and Password are required.');
      return;
    }
    if (password.length < MIN_PASSWORD_LENGTH) {
      setAccountError(`Password must be at least ${MIN_PASSWORD_LENGTH} characters.`);
      return;
    }

    setIsSavingAccount(true);
    try {
      await gridManager.setupOfflineAccount(firstName, lastName, password);
      setNeedsAccountSetup(false);
      // Do not keep the plaintext password in component state once it is hashed.
      setPassword('');
    } catch (err: any) {
      gridLog.captureError(LOG_COMPONENTS.USER, 'Could not create the local account.', err);
      setAccountError(err?.message || 'Could not create the local account.');
    } finally {
      setIsSavingAccount(false);
    }
  };

  const handleAssetUpload = (e: React.FormEvent) => {
    e.preventDefault();
    if (!assetName || !assetData) {
      alert('Asset Name and Data are required.');
      return;
    }
    assetManager.uploadAsset(assetName, assetType, assetData);
    setAssetName('');
    setAssetData('');
    setAssetsList(assetManager.getAllAssets());
  };

  const handleCacheChange = (newMB: number) => {
    setCacheMB(newMB);
    const updated = cacheManager.setMaxSizeMB(newMB);
    setCacheSettings(updated);
  };

  const handleClearCache = () => {
    cacheManager.clearCache();
    assetManager.resetLoadedAssets();
    setAssetsList(assetManager.getAllAssets());
  };

  const handleImportOar = async () => {
    if (!oarXmlInput) return;
    try {
      const parser = new OARParser(gridLog);
      const result = await parser.parseOAR(oarXmlInput);
      gridManager.getServer().addRegion(result.region);
      setImportedStatus(
        `Successfully imported region "${result.region.name}" with ${result.region.prims.length} objects.`
      );
      setOarXmlInput('');
    } catch (err: any) {
      gridLog.captureError(LOG_COMPONENTS.ARCHIVER, 'OAR import failed.', err);
      setImportedStatus('');
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'sans-serif', maxWidth: '800px', margin: '0 auto', color: '#333' }}>
      <h2>Offline OpenSim Grid Control Center</h2>

      <div style={{ background: '#f5f5f5', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
        <h3>Local Grid Status: <span style={{ color: isRunning ? 'green' : 'red' }}>{isRunning ? 'WORKING (ONLINE)' : 'SHUTDOWN (OFFLINE)'}</span></h3>
        <button
          onClick={handleToggleState}
          style={{
            padding: '10px 20px',
            fontSize: '16px',
            background: isRunning ? '#d9534f' : '#5cb85c',
            color: '#fff',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
        >
          {isRunning ? 'Shutdown Local Grid' : 'Start Local Grid'}
        </button>
      </div>

      {needsAccountSetup && (
        <div style={{ background: '#fff3cd', border: '1px solid #ffe8a1', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
          <h3>First Time Offline Account Setup</h3>
          <form onSubmit={handleAccountSubmit}>
            <div style={{ marginBottom: '10px' }}>
              <label htmlFor="offline-first-name">First Name: </label>
              <input id="offline-first-name" type="text" value={firstName} onChange={e => setFirstName(e.target.value)} placeholder="e.g. Local" required />
            </div>
            <div style={{ marginBottom: '10px' }}>
              <label htmlFor="offline-last-name">Last Name: </label>
              <input id="offline-last-name" type="text" value={lastName} onChange={e => setLastName(e.target.value)} placeholder="e.g. Resident" />
            </div>
            <div style={{ marginBottom: '10px' }}>
              <label htmlFor="offline-password">Password: </label>
              <input
                id="offline-password"
                type="password"
                value={password}
                onChange={e => setPassword(e.target.value)}
                minLength={MIN_PASSWORD_LENGTH}
                autoComplete="new-password"
                required
              />
              <p style={{ fontSize: '12px', color: '#666', margin: '4px 0 0' }}>
                Stored on this device as a salted PBKDF2-SHA256 hash. It is never saved in readable form and cannot be recovered.
              </p>
            </div>
            {accountError && <p style={{ color: '#c9302c', margin: '0 0 10px' }} role="alert">{accountError}</p>}
            <button
              type="submit"
              disabled={isSavingAccount}
              style={{ padding: '8px 16px', background: '#0275d8', color: '#fff', border: 'none', borderRadius: '4px' }}
            >
              {isSavingAccount ? 'Securing Account...' : 'Save Account'}
            </button>
          </form>
        </div>
      )}

      <GridConsolePanel console={gridLog} />

      <div style={{ background: '#f8f9fa', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
        <h3>OAR Backup Importer</h3>
        <p>Paste SceneObjectGroup XML string or OAR data to populate local region scene:</p>
        <textarea
          rows={4}
          style={{ width: '100%', marginBottom: '10px' }}
          value={oarXmlInput}
          onChange={e => setOarXmlInput(e.target.value)}
          placeholder="<SceneObjectGroup>...</SceneObjectGroup>"
          aria-label="OAR scene XML"
        />
        <button onClick={handleImportOar} style={{ padding: '8px 16px', background: '#6c757d', color: '#fff', border: 'none', borderRadius: '4px' }}>Import OAR Scene</button>
        {importedStatus && <p style={{ color: 'green', marginTop: '10px' }}>{importedStatus}</p>}
      </div>

      <div style={{ background: '#f8f9fa', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
        <h3>Local Asset Upload</h3>
        <form onSubmit={handleAssetUpload}>
          <div style={{ marginBottom: '10px' }}>
            <label htmlFor="asset-name">Asset Name: </label>
            <input id="asset-name" type="text" value={assetName} onChange={e => setAssetName(e.target.value)} placeholder="e.g. Custom Texture" required />
          </div>
          <div style={{ marginBottom: '10px' }}>
            <label htmlFor="asset-type">Asset Type: </label>
            <select id="asset-type" value={assetType} onChange={e => setAssetType(e.target.value as AssetType)}>
              <option value="texture">Texture</option>
              <option value="sound">Sound</option>
              <option value="script">Script</option>
              <option value="mesh">Mesh / Model</option>
              <option value="clothing">Clothing</option>
            </select>
          </div>
          <div style={{ marginBottom: '10px' }}>
            <label htmlFor="asset-data">Asset Data (String / Base64): </label>
            <input id="asset-data" type="text" value={assetData} onChange={e => setAssetData(e.target.value)} style={{ width: '60%' }} required />
          </div>
          <button type="submit" style={{ padding: '8px 16px', background: '#0275d8', color: '#fff', border: 'none', borderRadius: '4px' }}>Upload Asset</button>
        </form>
        <h4>Persisted Local Assets ({assetsList.length})</h4>
        <ul>
          {assetsList.map(a => (
            <li key={a.id}>
              <strong>{a.name}</strong> ({a.assetType}) - {a.sizeBytes} bytes - ID: {a.id}
            </li>
          ))}
        </ul>
      </div>

      <div style={{ background: '#f8f9fa', padding: '15px', borderRadius: '8px', marginBottom: '20px' }}>
        <h3>Storage &amp; Cache Sizing Options</h3>
        <p>Customizable cache storage size limit (256 MB up to 1,000,000 MB [~1 TB]):</p>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="cache-mb">Max Cache Size (MB): </label>
          <input
            id="cache-mb"
            type="number"
            min={256}
            max={1000000}
            value={cacheMB}
            onChange={e => handleCacheChange(Number(e.target.value))}
            style={{ width: '120px', padding: '4px', marginRight: '10px' }}
          />
          <span>({(cacheMB / 1024).toFixed(2)} GB)</span>
        </div>
        <div style={{ marginBottom: '15px' }}>
          <input
            type="range"
            min={256}
            max={1000000}
            value={cacheMB}
            onChange={e => handleCacheChange(Number(e.target.value))}
            style={{ width: '100%' }}
            aria-label="Max cache size in megabytes"
          />
        </div>
        <button onClick={handleClearCache} style={{ padding: '8px 16px', background: '#f0ad4e', color: '#fff', border: 'none', borderRadius: '4px' }}>Clear Offline Cache</button>
      </div>
    </div>
  );
};
