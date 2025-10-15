/**
 * Linkpoint PWA - Authentication Module
 */

class AuthManager extends Utils.EventEmitter {
  constructor(protocolManager) {
    super();
    this.protocol = protocolManager;
    this.user = null;
    this.credentials = null;
  }

  /**
   * Initialize authentication
   */
  init() {
    // Check for saved credentials
    const savedCreds = Utils.storage.get('linkpoint_credentials');
    if (savedCreds && savedCreds.rememberMe) {
      this.credentials = savedCreds;
      this.emit('credentials_loaded', savedCreds);
    }

    // Setup login form
    this.setupLoginForm();
  }

  /**
   * Setup login form handlers
   */
  setupLoginForm() {
    const loginForm = document.getElementById('login-form');
    const gridSelect = document.getElementById('grid-select');
    const customGridGroup = document.getElementById('custom-grid-group');

    if (!loginForm) return;

    // Show/hide custom grid URL field
    gridSelect?.addEventListener('change', (e) => {
      if (customGridGroup) {
        customGridGroup.style.display = e.target.value === 'custom' ? 'block' : 'none';
      }
    });

    // Handle login form submission
    loginForm.addEventListener('submit', async (e) => {
      e.preventDefault();
      await this.handleLogin();
    });

    // Load saved credentials if any
    if (this.credentials) {
      const usernameInput = document.getElementById('username');
      const rememberCheckbox = document.getElementById('remember-me');
      
      if (usernameInput && this.credentials.username) {
        usernameInput.value = this.credentials.username;
      }
      if (rememberCheckbox) {
        rememberCheckbox.checked = this.credentials.rememberMe;
      }
      if (gridSelect && this.credentials.grid) {
        gridSelect.value = this.credentials.grid;
      }
    }
  }

  /**
   * Handle login process
   */
  async handleLogin() {
    const gridSelect = document.getElementById('grid-select');
    const usernameInput = document.getElementById('username');
    const passwordInput = document.getElementById('password');
    const rememberCheckbox = document.getElementById('remember-me');
    const submitBtn = document.querySelector('#login-form button[type="submit"]');
    const btnText = submitBtn?.querySelector('.btn-text');
    const btnLoader = submitBtn?.querySelector('.btn-loader');

    if (!gridSelect || !usernameInput || !passwordInput) return;

    const grid = gridSelect.value;
    const username = usernameInput.value.trim();
    const password = passwordInput.value;
    const rememberMe = rememberCheckbox?.checked || false;

    // Validate inputs
    if (!username || !password) {
      Utils.showToast('Please enter username and password', 'error');
      return;
    }

    // Custom grid validation
    if (grid === 'custom') {
      const customGridUrl = document.getElementById('custom-grid-url');
      if (!customGridUrl || !customGridUrl.value) {
        Utils.showToast('Please enter custom grid URL', 'error');
        return;
      }
    }

    // Show loading state
    if (submitBtn) {
      submitBtn.disabled = true;
      if (btnText) btnText.style.display = 'none';
      if (btnLoader) btnLoader.style.display = 'inline-block';
    }

    try {
      // Attempt login
      const response = await this.protocol.login(grid, username, password);

      // Store credentials if remember me is checked
      if (rememberMe) {
        Utils.storage.set('linkpoint_credentials', {
          username,
          grid,
          rememberMe: true
        });
      } else {
        Utils.storage.remove('linkpoint_credentials');
      }

      // Set user data
      this.user = {
        id: response.agent_id,
        sessionId: response.session_id,
        firstName: response.first_name,
        lastName: response.last_name,
        fullName: `${response.first_name} ${response.last_name}`,
        grid,
        loginTime: Date.now()
      };

      // Update UI
      this.updateUserInfo();

      // Emit login success event
      this.emit('login_success', this.user);

      Utils.showToast(`Welcome, ${this.user.fullName}!`, 'success');

      // Switch to world view
      setTimeout(() => {
        window.app?.switchView('world');
      }, 1000);

    } catch (error) {
      console.error('Login error:', error);
      Utils.showToast(error.message || 'Login failed. Please try again.', 'error');
      this.emit('login_failed', error);
    } finally {
      // Reset button state
      if (submitBtn) {
        submitBtn.disabled = false;
        if (btnText) btnText.style.display = 'inline';
        if (btnLoader) btnLoader.style.display = 'none';
      }
    }
  }

  /**
   * Update user info in UI
   */
  updateUserInfo() {
    const userInfo = document.getElementById('user-info');
    if (!userInfo || !this.user) return;

    const userName = userInfo.querySelector('.user-name');
    const userStatus = userInfo.querySelector('.user-status');

    if (userName) {
      userName.textContent = this.user.fullName;
    }

    if (userStatus) {
      userStatus.textContent = 'Online';
      userStatus.style.color = 'var(--success-color)';
    }

    // Update connection status
    const connectionStatus = document.getElementById('connection-status');
    if (connectionStatus) {
      connectionStatus.textContent = 'Connected';
      connectionStatus.style.color = 'var(--success-color)';
    }
  }

  /**
   * Logout
   */
  async logout() {
    try {
      await this.protocol.logout();

      // Clear user data
      this.user = null;

      // Update UI
      const userInfo = document.getElementById('user-info');
      if (userInfo) {
        const userName = userInfo.querySelector('.user-name');
        const userStatus = userInfo.querySelector('.user-status');

        if (userName) userName.textContent = 'Not logged in';
        if (userStatus) {
          userStatus.textContent = 'Offline';
          userStatus.style.color = 'var(--text-secondary)';
        }
      }

      // Update connection status
      const connectionStatus = document.getElementById('connection-status');
      if (connectionStatus) {
        connectionStatus.textContent = 'Offline';
        connectionStatus.style.color = 'var(--text-secondary)';
      }

      // Clear password field
      const passwordInput = document.getElementById('password');
      if (passwordInput) passwordInput.value = '';

      // Emit logout event
      this.emit('logout');

      Utils.showToast('Logged out successfully', 'success');

      // Switch to login view
      window.app?.switchView('login');

    } catch (error) {
      console.error('Logout error:', error);
      Utils.showToast('Error during logout', 'error');
    }
  }

  /**
   * Get current user
   */
  getUser() {
    return this.user;
  }

  /**
   * Check if user is logged in
   */
  isLoggedIn() {
    return this.user !== null && this.protocol.isConnected();
  }

  /**
   * Get user display name
   */
  getUserDisplayName() {
    return this.user ? this.user.fullName : 'Guest';
  }

  /**
   * Auto-login if credentials are saved
   */
  async tryAutoLogin() {
    if (!this.credentials || !this.credentials.rememberMe) {
      return false;
    }

    // Fill in the form
    const usernameInput = document.getElementById('username');
    if (usernameInput && this.credentials.username) {
      usernameInput.value = this.credentials.username;
    }

    // Don't auto-submit for security reasons
    // User must enter password and click login
    return true;
  }
}

// Make available globally
window.AuthManager = AuthManager;
