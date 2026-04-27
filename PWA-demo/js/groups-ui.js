/**
 * Linkpoint PWA - Groups UI Manager
 * Handles Groups UI and integration with GroupsManager
 */

class GroupsUIManager extends Utils.EventEmitter {
  constructor(protocolManager, authManager) {
    super();
    this.protocol = protocolManager;
    this.auth = authManager;
    this.groupsManager = new GroupsManager();
    this.currentGroupId = null;
    
    // UI Elements
    this.groupsList = null;
    this.groupDetails = null;
    this.createGroupBtn = null;
    this.leaveGroupBtn = null;
  }

  /**
   * Initialize groups UI
   */
  init() {
    console.log('[GroupsUI] Initializing...');
    
    // Get UI elements
    this.groupsList = document.getElementById('groups-list');
    this.groupDetails = document.getElementById('group-details');
    this.createGroupBtn = document.getElementById('create-group-btn');
    this.leaveGroupBtn = document.getElementById('leave-group-btn');
    
    if (!this.groupsList || !this.groupDetails) {
      console.error('[GroupsUI] Required elements not found');
      return;
    }

    // Setup event listeners
    this.setupEventListeners();
    
    // Setup protocol listeners
    this.protocol.on('group_update', (data) => this.handleGroupUpdate(data));
    this.protocol.on('group_member_update', (data) => this.handleMemberUpdate(data));
    this.protocol.on('group_notice', (data) => this.handleGroupNotice(data));
    
    console.log('[GroupsUI] Initialized');
  }

  /**
   * Setup UI event listeners
   */
  setupEventListeners() {
    // Create group button
    if (this.createGroupBtn) {
      this.createGroupBtn.addEventListener('click', () => this.showCreateGroupDialog());
    }
    
    // Leave group button
    if (this.leaveGroupBtn) {
      this.leaveGroupBtn.addEventListener('click', () => this.leaveCurrentGroup());
    }
    
    // Group tabs
    const groupTabs = document.querySelectorAll('.group-tab');
    groupTabs.forEach(tab => {
      tab.addEventListener('click', (e) => {
        const tabName = e.target.dataset.tab;
        this.switchGroupTab(tabName);
      });
    });
  }

  /**
   * Load user's groups
   */
  async loadGroups() {
    if (!this.auth.isLoggedIn()) {
      this.showPlaceholder('Please login to view groups');
      return;
    }

    try {
      // Request groups from protocol
      if (this.protocol.requestGroups) {
        const groups = await this.protocol.requestGroups();
        this.displayGroups(groups);
      } else {
        // Show demo groups for now
        this.showDemoGroups();
      }
    } catch (error) {
      console.error('[GroupsUI] Error loading groups:', error);
      Utils.showToast('Failed to load groups', 'error');
    }
  }

  /**
   * Display groups list
   */
  displayGroups(groups) {
    if (!this.groupsList) return;
    
    if (!groups || groups.length === 0) {
      this.showPlaceholder('No groups. Create or join one!');
      return;
    }

    this.groupsList.innerHTML = '';
    
    groups.forEach(group => {
      const groupItem = document.createElement('div');
      groupItem.className = 'group-item';
      groupItem.dataset.groupId = group.id;
      
      groupItem.innerHTML = `
        <div class="group-item-content">
          <div class="group-icon">👨‍👨‍👧‍👦</div>
          <div class="group-info">
            <div class="group-name">${this.escapeHtml(group.name)}</div>
            <div class="group-role">${this.escapeHtml(group.role || 'Member')}</div>
          </div>
          <div class="group-badge">
            ${group.memberCount || '0'} members
          </div>
        </div>
      `;
      
      groupItem.addEventListener('click', () => this.selectGroup(group.id));
      this.groupsList.appendChild(groupItem);
    });
  }

  /**
   * Show placeholder message
   */
  showPlaceholder(message) {
    if (this.groupsList) {
      this.groupsList.innerHTML = `<p class="placeholder-text">${message}</p>`;
    }
  }

  /**
   * Show demo groups for testing
   */
  showDemoGroups() {
    const demoGroups = [
      { id: 'demo-1', name: 'Virtual World Explorers', role: 'Member', memberCount: 42 },
      { id: 'demo-2', name: 'Builders United', role: 'Officer', memberCount: 28 },
      { id: 'demo-3', name: 'Social Hub', role: 'Member', memberCount: 156 }
    ];
    
    this.displayGroups(demoGroups);
  }

  /**
   * Select and display group details
   */
  async selectGroup(groupId) {
    this.currentGroupId = groupId;
    
    try {
      // Get group info
      let groupInfo = this.groupsManager.getGroupInfo(groupId);
      
      if (!groupInfo) {
        // Request from protocol
        if (this.protocol.requestGroupInfo) {
          groupInfo = await this.protocol.requestGroupInfo(groupId);
        } else {
          // Show demo data
          groupInfo = {
            id: groupId,
            name: 'Group Name',
            charter: 'This is a demo group charter. In a real implementation, this would fetch from the server.',
            founder: 'Founder Name',
            membershipFee: 0,
            openEnrollment: true
          };
        }
        
        this.groupsManager.setGroupInfo(groupId, groupInfo);
      }
      
      // Display group details
      this.displayGroupDetails(groupInfo);
      
      // Load group members
      this.loadGroupMembers(groupId);
      
      // Show details panel
      if (this.groupDetails) {
        this.groupDetails.style.display = 'block';
      }
      
    } catch (error) {
      console.error('[GroupsUI] Error selecting group:', error);
      Utils.showToast('Failed to load group details', 'error');
    }
  }

  /**
   * Display group details
   */
  displayGroupDetails(groupInfo) {
    // Update group name
    const nameEl = document.getElementById('group-detail-name');
    if (nameEl) {
      nameEl.textContent = groupInfo.name;
    }
    
    // Update group info
    const charterEl = document.getElementById('group-charter');
    if (charterEl) {
      charterEl.textContent = groupInfo.charter || 'No charter available';
    }
    
    const founderEl = document.getElementById('group-founder');
    if (founderEl) {
      founderEl.textContent = groupInfo.founder || 'Unknown';
    }
    
    const feeEl = document.getElementById('group-fee');
    if (feeEl) {
      feeEl.textContent = groupInfo.membershipFee || '0';
    }
    
    const enrollmentEl = document.getElementById('group-enrollment');
    if (enrollmentEl) {
      enrollmentEl.textContent = groupInfo.openEnrollment ? 'Yes' : 'No';
    }
  }

  /**
   * Load group members
   */
  async loadGroupMembers(groupId) {
    const membersList = document.getElementById('group-members-list');
    if (!membersList) return;
    
    try {
      let members = this.groupsManager.getGroupMembers(groupId);
      
      if (!members || members.size === 0) {
        // Request from protocol or show demo
        if (this.protocol.requestGroupMembers) {
          members = await this.protocol.requestGroupMembers(groupId);
        } else {
          // Demo members
          membersList.innerHTML = `
            <div class="member-item">
              <div class="member-name">Member 1</div>
              <div class="member-title">Member</div>
            </div>
            <div class="member-item">
              <div class="member-name">Member 2</div>
              <div class="member-title">Officer</div>
            </div>
          `;
          return;
        }
      }
      
      membersList.innerHTML = '';
      members.forEach(member => {
        const memberEl = document.createElement('div');
        memberEl.className = 'member-item';
        memberEl.innerHTML = `
          <div class="member-name">${this.escapeHtml(member.name || member.id)}</div>
          <div class="member-title">${this.escapeHtml(member.title || 'Member')}</div>
        `;
        membersList.appendChild(memberEl);
      });
      
    } catch (error) {
      console.error('[GroupsUI] Error loading members:', error);
      membersList.innerHTML = '<p class="placeholder-text">Failed to load members</p>';
    }
  }

  /**
   * Switch group tab
   */
  switchGroupTab(tabName) {
    // Update tab buttons
    document.querySelectorAll('.group-tab').forEach(tab => {
      tab.classList.remove('active');
      if (tab.dataset.tab === tabName) {
        tab.classList.add('active');
      }
    });
    
    // Update tab panels
    document.querySelectorAll('.group-tab-panel').forEach(panel => {
      panel.classList.remove('active');
    });
    
    const activePanel = document.getElementById(`group-${tabName}-tab`);
    if (activePanel) {
      activePanel.classList.add('active');
    }
    
    // Load data for the selected tab
    switch (tabName) {
      case 'members':
        if (this.currentGroupId) {
          this.loadGroupMembers(this.currentGroupId);
        }
        break;
      case 'roles':
        if (this.currentGroupId) {
          this.loadGroupRoles(this.currentGroupId);
        }
        break;
      case 'notices':
        if (this.currentGroupId) {
          this.loadGroupNotices(this.currentGroupId);
        }
        break;
    }
  }

  /**
   * Load group roles
   */
  async loadGroupRoles(groupId) {
    const rolesList = document.getElementById('group-roles-list');
    if (!rolesList) return;
    
    rolesList.innerHTML = `
      <div class="role-item">
        <div class="role-name">Everyone</div>
        <div class="role-description">Default role for all members</div>
      </div>
      <div class="role-item">
        <div class="role-name">Officers</div>
        <div class="role-description">Group officers with elevated permissions</div>
      </div>
    `;
  }

  /**
   * Load group notices
   */
  async loadGroupNotices(groupId) {
    const noticesList = document.getElementById('group-notices-list');
    if (!noticesList) return;
    
    const notices = this.groupsManager.getGroupNotices(groupId);
    
    if (!notices || notices.length === 0) {
      noticesList.innerHTML = '<p class="placeholder-text">No notices</p>';
      return;
    }
    
    noticesList.innerHTML = '';
    notices.forEach(notice => {
      const noticeEl = document.createElement('div');
      noticeEl.className = 'notice-item';
      noticeEl.innerHTML = `
        <div class="notice-subject">${this.escapeHtml(notice.subject)}</div>
        <div class="notice-from">From: ${this.escapeHtml(notice.from)}</div>
        <div class="notice-date">${Utils.formatTime(notice.timestamp)}</div>
        <div class="notice-message">${this.escapeHtml(notice.message)}</div>
      `;
      noticesList.appendChild(noticeEl);
    });
  }

  /**
   * Show create group dialog
   */
  showCreateGroupDialog() {
    Utils.showToast('Group creation is not yet implemented', 'info');
  }

  /**
   * Leave current group
   */
  async leaveCurrentGroup() {
    if (!this.currentGroupId) return;
    
    const confirmed = confirm('Are you sure you want to leave this group?');
    if (!confirmed) return;
    
    try {
      // Send leave group request
      if (this.protocol.leaveGroup) {
        await this.protocol.leaveGroup(this.currentGroupId);
      }
      
      Utils.showToast('Left group successfully', 'success');
      
      // Hide details and reload groups
      if (this.groupDetails) {
        this.groupDetails.style.display = 'none';
      }
      this.currentGroupId = null;
      this.loadGroups();
      
    } catch (error) {
      console.error('[GroupsUI] Error leaving group:', error);
      Utils.showToast('Failed to leave group', 'error');
    }
  }

  /**
   * Handle group update from protocol
   */
  handleGroupUpdate(data) {
    console.log('[GroupsUI] Group update:', data);
    this.loadGroups();
  }

  /**
   * Handle member update from protocol
   */
  handleMemberUpdate(data) {
    console.log('[GroupsUI] Member update:', data);
    if (this.currentGroupId === data.groupId) {
      this.loadGroupMembers(this.currentGroupId);
    }
  }

  /**
   * Handle group notice
   */
  handleGroupNotice(data) {
    console.log('[GroupsUI] Group notice:', data);
    
    // Add to groups manager
    this.groupsManager.addGroupNotice(data.groupId, data);
    
    // Show notification
    Utils.showToast(`New notice in ${data.groupName}`, 'info');
    
    // Refresh if viewing notices
    if (this.currentGroupId === data.groupId) {
      const activeTab = document.querySelector('.group-tab.active');
      if (activeTab && activeTab.dataset.tab === 'notices') {
        this.loadGroupNotices(this.currentGroupId);
      }
    }
  }

  /**
   * Send group chat message
   */
  async sendGroupMessage(groupId, message) {
    try {
      await this.groupsManager.sendGroupChat(groupId, message);
      this.emit('group_message_sent', { groupId, message });
    } catch (error) {
      console.error('[GroupsUI] Error sending group message:', error);
      throw error;
    }
  }

  /**
   * Escape HTML to prevent XSS
   */
  escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
  }

  /**
   * Get groups manager instance
   */
  getGroupsManager() {
    return this.groupsManager;
  }
}

// Make available globally
window.GroupsUIManager = GroupsUIManager;
