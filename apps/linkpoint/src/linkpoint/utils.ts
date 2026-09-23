/**
 * Linkpoint PWA - Utility Functions
 */

export const Utils = {
  generateUUID() {
    if (typeof globalThis.crypto !== 'undefined' && typeof globalThis.crypto.randomUUID === 'function') {
      return globalThis.crypto.randomUUID();
    }
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
      const r = Math.random() * 16 | 0;
      const v = c === 'x' ? r : (r & 0x3 | 0x8);
      return v.toString(16);
    });
  },

  formatTime(timestamp: number) {
    const date = new Date(timestamp);
    return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' });
  },

  showToast(message: string, type: string = 'info', duration: number = 3000) {
    if (typeof document === 'undefined') return;
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast ${type} p-4 mb-2 rounded shadow-lg transition-all duration-300 transform translate-x-0`;

    if (type === 'success') toast.classList.add('bg-green-600', 'text-white');
    else if (type === 'error') toast.classList.add('bg-red-600', 'text-white');
    else if (type === 'warning') toast.classList.add('bg-yellow-500', 'text-black');
    else toast.classList.add('bg-blue-600', 'text-white');

    const escapeHTML = (str: string) => String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;').replace(/'/g, '&#039;');
    toast.innerHTML = `
      <div class="toast-content">
        <strong>${escapeHTML(type).charAt(0).toUpperCase() + escapeHTML(type).slice(1)}</strong>
        <p>${escapeHTML(message)}</p>
      </div>
    `;

    container.appendChild(toast);

    setTimeout(() => {
      toast.style.opacity = '0';
      toast.style.transform = 'translateX(100%)';
      setTimeout(() => {
        if (container.contains(toast)) container.removeChild(toast);
      }, 300);
    }, duration);
  },

  storage: {
    memoryStore: new Map<string, string>(),

    get(key: string, defaultValue: any = null) {
      try {
        if (typeof localStorage !== 'undefined') {
          const item = localStorage.getItem(key);
          return item ? JSON.parse(item) : defaultValue;
        } else {
          const item = this.memoryStore.get(key);
          return item ? JSON.parse(item) : defaultValue;
        }
      } catch (e) {
        return defaultValue;
      }
    },

    set(key: string, value: any) {
      try {
        if (typeof localStorage !== 'undefined') {
          localStorage.setItem(key, JSON.stringify(value));
        } else {
          this.memoryStore.set(key, JSON.stringify(value));
        }
        return true;
      } catch (e) {
        return false;
      }
    },

    remove(key: string) {
      try {
        if (typeof localStorage !== 'undefined') {
          localStorage.removeItem(key);
        } else {
          this.memoryStore.delete(key);
        }
        return true;
      } catch (e) {
        return false;
      }
    }
  },

  debounce(func: Function, wait: number) {
    let timeout: any;
    return function executedFunction(...args: any[]) {
      const later = () => {
        clearTimeout(timeout);
        func(...args);
      };
      clearTimeout(timeout);
      timeout = setTimeout(later, wait);
    };
  },

  throttle(func: Function, limit: number) {
    let inThrottle: boolean;
    return function(this: any, ...args: any[]) {
      if (!inThrottle) {
        func.apply(this, args);
        inThrottle = true;
        setTimeout(() => inThrottle = false, limit);
      }
    };
  },

  deepClone(obj: any) {
    return JSON.parse(JSON.stringify(obj));
  },

  formatFileSize(bytes: number) {
    if (bytes === 0) return '0 Bytes';
    const isNegative = bytes < 0;
    const absBytes = Math.abs(bytes);
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB'];
    const i = Math.floor(Math.log(absBytes) / Math.log(k));
    const sizeIndex = Math.min(i, sizes.length - 1);
    const value = Math.round(absBytes / Math.pow(k, sizeIndex) * 100) / 100;
    return (isNegative ? '-' : '') + value + ' ' + sizes[sizeIndex];
  },

  parseQueryString(url: string = typeof window !== 'undefined' ? window.location.href : '') {
    const params: Record<string, string> = {};
    const queryString = url.split('?')[1];
    if (queryString) {
      queryString.split('&').forEach(param => {
        const [key, value] = param.split('=');
        params[decodeURIComponent(key)] = decodeURIComponent(value || '');
      });
    }
    return params;
  },

  isValidEmail(email: string) {
    const re = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return re.test(email);
  },

  clamp(value: number, min: number, max: number) {
    return Math.min(Math.max(value, min), max);
  },

  lerp(start: number, end: number, t: number) {
    return start * (1 - t) + end * t;
  },

  isMobile() {
    if (typeof navigator === 'undefined') return false;
    return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent);
  },

  getDeviceInfo() {
    if (typeof navigator === 'undefined') {
      return {
        userAgent: 'Node.js',
        platform: 'Server',
        vendor: '',
        language: 'en',
        isMobile: false,
        isOnline: true,
        cookiesEnabled: false
      };
    }
    return {
      userAgent: navigator.userAgent,
      platform: navigator.platform,
      vendor: navigator.vendor,
      language: navigator.language,
      isMobile: this.isMobile(),
      isOnline: navigator.onLine,
      cookiesEnabled: navigator.cookieEnabled
    };
  },

  sleep(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  },

  async retry(fn: Function, retries: number = 3, delay: number = 1000): Promise<any> {
    try {
      return await fn();
    } catch (error) {
      if (retries === 0) throw error;
      await this.sleep(delay);
      return this.retry(fn, retries - 1, delay * 2);
    }
  },

  EventEmitter: class {
    private events: Record<string, Function[]> = {};

    on(event: string, listener: Function) {
      if (!this.events[event]) {
        this.events[event] = [];
      }
      this.events[event].push(listener);
    }

    off(event: string, listener: Function) {
      if (!this.events[event]) return;
      this.events[event] = this.events[event].filter(l => l !== listener);
    }

    emit(event: string, ...args: any[]) {
      if (!this.events[event]) return;
      this.events[event].forEach(listener => listener(...args));
    }

    once(event: string, listener: Function) {
      const onceWrapper = (...args: any[]) => {
        listener(...args);
        this.off(event, onceWrapper);
      };
      this.on(event, onceWrapper);
    }
  }
};
