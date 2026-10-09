// @vitest-environment jsdom
import { beforeEach, describe, expect, it, vi } from 'vitest';
import { CapabilitiesManager } from '../phase2/capabilities';
import { GroupsManager } from '../phase2/groups';
import { NoticeStore } from '../notices';
import { Utils } from '../utils';

describe('Dual-transport Group Notices & TTL Caching', () => {
  beforeEach(() => {
    localStorage.clear();
    vi.restoreAllMocks();
  });

  it('Requirement 1: CapabilitiesManager detects and exposes GroupNoticesList capability URL', async () => {
    const caps = new CapabilitiesManager();
    expect(caps.getGroupNoticesListUrl()).toBeNull();

    await caps.parseSeedCapability({
      GroupNoticesList: 'https://sim.example.com/cap/group_notices_list_123',
    });

    expect(caps.hasCapability('GroupNoticesList')).toBe(true);
    expect(caps.getGroupNoticesListUrl()).toBe('https://sim.example.com/cap/group_notices_list_123');
  });

  it('Requirement 2: GroupsManager queries HTTP capability when available and updates NoticeStore', async () => {
    const caps = new CapabilitiesManager();
    caps.setCapability('GroupNoticesList', 'https://sim.example.com/cap/group_notices_list_123');

    const store = new NoticeStore();
    const groups = new GroupsManager(null, caps, store);

    const mockResponseXml = `<llsd>
      <map>
        <key>notice_id</key><string>n-http-1</string>
        <key>group_id</key><uuid>group-100</uuid>
        <key>subject</key><string>HTTP Notice</string>
        <key>message</key><string>Content via HTTP cap</string>
        <key>from_name</key><string>Grid Admin</string>
        <key>timestamp</key><integer>1700000000</integer>
        <key>has_attachment</key><boolean>1</boolean>
        <key>attachment_name</key><string>Free Gift</string>
      </map>
    </llsd>`;

    global.fetch = vi.fn().mockResolvedValue({
      ok: true,
      text: async () => mockResponseXml,
    } as any);

    const notices = await groups.requestGroupNotices('group-100');

    expect(global.fetch).toHaveBeenCalledWith(
      'https://sim.example.com/cap/group_notices_list_123',
      expect.objectContaining({
        method: 'POST',
        headers: { 'Content-Type': 'application/llsd+xml' },
      })
    );

    expect(notices).toHaveLength(1);
    expect(notices[0]).toMatchObject({
      id: 'n-http-1',
      subject: 'HTTP Notice',
      message: 'Content via HTTP cap',
      from: 'Grid Admin',
      hasAttachment: true,
    });

    // Check NoticeStore cache
    expect(store.isGroupCacheValid('group-100')).toBe(true);
    expect(store.get('n-http-1')).not.toBeNull();
  });

  it('Requirement 2 & 5: GroupsManager falls back to UDP GroupNoticesListRequest when HTTP capability fails or times out', async () => {
    const caps = new CapabilitiesManager();
    caps.setCapability('GroupNoticesList', 'https://sim.example.com/cap/failing_cap');

    const protocol = new Utils.EventEmitter() as any;
    protocol.send = vi.fn();

    const store = new NoticeStore(protocol);
    const groups = new GroupsManager(protocol, caps, store);

    // Mock HTTP failure
    global.fetch = vi.fn().mockRejectedValue(new Error('Network error'));

    // Simulate async UDP reply from grid
    protocol.send = vi.fn().mockImplementation((msgName: string, payload: any) => {
      if (msgName === 'GroupNoticesListRequest') {
        setTimeout(() => {
          protocol.emit('GroupNoticesListReply', {
            groupId: 'group-200',
            notices: [
              {
                id: 'n-udp-1',
                subject: 'UDP Notice',
                message: 'Content via UDP',
                from: 'Legacy Server',
                timestamp: 1700000500,
              },
            ],
          });
        }, 10);
      }
    });

    const notices = await groups.requestGroupNotices('group-200');

    expect(protocol.send).toHaveBeenCalledWith('GroupNoticesListRequest', { GroupData: { GroupID: 'group-200' } });
    expect(notices).toHaveLength(1);
    expect(notices[0].subject).toBe('UDP Notice');
    expect(store.isGroupCacheValid('group-200')).toBe(true);
  });

  it('Requirement 3: Notice queries within 5-minute TTL window return cached data without network requests', async () => {
    const caps = new CapabilitiesManager();
    caps.setCapability('GroupNoticesList', 'https://sim.example.com/cap/group_notices_list_123');

    const store = new NoticeStore();
    const groups = new GroupsManager(null, caps, store);

    const fetchSpy = vi.fn().mockResolvedValue({
      ok: true,
      text: async () => JSON.stringify([{ id: 'n-1', subject: 'Notice 1' }]),
    } as any);
    global.fetch = fetchSpy;

    // First call fetches from network
    await groups.requestGroupNotices('group-300');
    expect(fetchSpy).toHaveBeenCalledTimes(1);

    // Second call within 5-minute TTL window should NOT make additional network calls
    const cachedNotices = await groups.requestGroupNotices('group-300');
    expect(fetchSpy).toHaveBeenCalledTimes(1); // Call count remains 1!
    expect(cachedNotices).toHaveLength(1);
    expect(cachedNotices[0].subject).toBe('Notice 1');
  });

  it('Requirement 4: Expired TTL entries trigger background re-validation request', async () => {
    const caps = new CapabilitiesManager();
    caps.setCapability('GroupNoticesList', 'https://sim.example.com/cap/group_notices_list_123');

    const store = new NoticeStore();
    const groups = new GroupsManager(null, caps, store);

    // Seed store with expired cache (6 minutes ago)
    const oldTimestamp = Date.now() - 6 * 60 * 1000;
    store.setGroupCache('group-400', [{ id: 'n-old', groupId: 'group-400', subject: 'Stale Notice', message: 'Old', from: 'System', timestamp: oldTimestamp, calendar: null }], oldTimestamp);

    const fetchSpy = vi.fn().mockResolvedValue({
      ok: true,
      text: async () => JSON.stringify([{ id: 'n-fresh', subject: 'Fresh Notice' }]),
    } as any);
    global.fetch = fetchSpy;

    // Query for expired group notice returns stale data immediately while triggering background revalidation
    const notices = await groups.requestGroupNotices('group-400');
    expect(notices[0].subject).toBe('Stale Notice');
    expect(fetchSpy).toHaveBeenCalledTimes(1);
  });

  it('Requirement 5: Marks history unavailable when both HTTP capability and UDP packet retries fail', async () => {
    const protocol = new Utils.EventEmitter() as any;
    protocol.send = vi.fn(); // No reply sent, so UDP times out

    const store = new NoticeStore(protocol);
    const groups = new GroupsManager(protocol, undefined, store);

    await expect(
      groups.requestGroupNotices('group-500', { timeoutMs: 50 })
    ).rejects.toThrow('Notice history unavailable');

    expect(store.isHistoryUnavailable('group-500')).toBe(true);
  });
});
