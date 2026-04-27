/**
 * Linkpoint PWA - Search System
 */

class SearchManager extends Utils.EventEmitter {
  constructor(protocolManager) {
    super();
    this.protocol = protocolManager;
    this.searchHistory = [];
    this.searchResults = [];
    this.categories = ['All', 'Places', 'Events', 'People', 'Groups', 'Classifieds'];
    this.currentCategory = 'All';
  }

  /**
   * Initialize search
   */
  async init() {
    this.loadSearchHistory();
    
    // Setup protocol listeners
    this.protocol.on('search_results', (data) => this.handleSearchResults(data));
  }

  /**
   * Search
   */
  async search(query, category = 'All') {
    if (!query || query.trim().length < 2) {
      Utils.showToast('Please enter at least 2 characters', 'warning');
      return;
    }

    const searchQuery = query.trim();
    this.currentCategory = category;

    try {
      // Send search request
      this.protocol.sendMessage('DirSearchRequest', {
        query: searchQuery,
        category: category.toLowerCase(),
        start_index: 0,
        limit: 50
      });

      // Add to search history
      this.addToHistory(searchQuery);

      Utils.showToast('Searching...', 'info');
      this.emit('search_started', { query: searchQuery, category });

      // Simulate search for demo
      await Utils.sleep(1000);
      this.createDemoResults(searchQuery, category);

    } catch (error) {
      console.error('Search failed:', error);
      Utils.showToast('Search failed', 'error');
      this.emit('search_failed', error);
    }
  }

  /**
   * Create demo search results
   */
  createDemoResults(query, category) {
    const demoResults = {
      'All': [
        { type: 'place', name: 'Sandbox Island', traffic: 42, description: 'Public sandbox for building' },
        { type: 'event', name: 'Live Music Night', date: '2025-10-20 20:00', location: 'Music Hall' },
        { type: 'person', name: 'John Resident', online: true, location: 'Welcome Area' },
        { type: 'group', name: 'Builders United', members: 156, description: 'For creators and builders' }
      ],
      'Places': [
        { type: 'place', name: 'Welcome Area', traffic: 89, description: 'Newcomer friendly zone' },
        { type: 'place', name: 'Sandbox Island', traffic: 42, description: 'Public sandbox for building' },
        { type: 'place', name: 'Shopping Mall', traffic: 67, description: 'Virtual marketplace' }
      ],
      'Events': [
        { type: 'event', name: 'Live Music Night', date: '2025-10-20 20:00', location: 'Music Hall' },
        { type: 'event', name: 'Art Gallery Opening', date: '2025-10-21 19:00', location: 'Art District' },
        { type: 'event', name: 'Dance Party', date: '2025-10-22 21:00', location: 'Club Retro' }
      ],
      'People': [
        { type: 'person', name: 'John Resident', online: true, location: 'Welcome Area' },
        { type: 'person', name: 'Jane Doe', online: false, lastSeen: '2 hours ago' },
        { type: 'person', name: 'Bob Builder', online: true, location: 'Sandbox' }
      ],
      'Groups': [
        { type: 'group', name: 'Builders United', members: 156, description: 'For creators and builders' },
        { type: 'group', name: 'Music Lovers', members: 234, description: 'Live music events' },
        { type: 'group', name: 'Explorers', members: 89, description: 'Discover new places' }
      ],
      'Classifieds': [
        { type: 'classified', title: 'Land for Sale', price: 'L$5000', location: 'Oceanview' },
        { type: 'classified', title: 'Custom Builds', price: 'Negotiable', category: 'Services' }
      ]
    };

    this.searchResults = demoResults[category] || demoResults['All'];
    this.handleSearchResults({ results: this.searchResults, query, category });
  }

  /**
   * Handle search results
   */
  handleSearchResults(data) {
    this.searchResults = data.results || [];
    
    Utils.showToast(`Found ${this.searchResults.length} results`, 'success');
    this.emit('search_completed', data);
  }

  /**
   * Search places
   */
  async searchPlaces(query) {
    return this.search(query, 'Places');
  }

  /**
   * Search events
   */
  async searchEvents(query) {
    return this.search(query, 'Events');
  }

  /**
   * Search people
   */
  async searchPeople(query) {
    return this.search(query, 'People');
  }

  /**
   * Search groups
   */
  async searchGroups(query) {
    return this.search(query, 'Groups');
  }

  /**
   * Get popular places
   */
  async getPopularPlaces() {
    try {
      this.protocol.sendMessage('PopularPlacesRequest', {});
      
      // Demo data
      return [
        { name: 'Welcome Area', traffic: 89 },
        { name: 'Shopping Mall', traffic: 67 },
        { name: 'Sandbox Island', traffic: 42 }
      ];
    } catch (error) {
      console.error('Failed to get popular places:', error);
      return [];
    }
  }

  /**
   * Get upcoming events
   */
  async getUpcomingEvents() {
    try {
      this.protocol.sendMessage('UpcomingEventsRequest', {});
      
      // Demo data
      return [
        { name: 'Live Music Night', date: '2025-10-20 20:00' },
        { name: 'Art Gallery Opening', date: '2025-10-21 19:00' },
        { name: 'Dance Party', date: '2025-10-22 21:00' }
      ];
    } catch (error) {
      console.error('Failed to get upcoming events:', error);
      return [];
    }
  }

  /**
   * Add to search history
   */
  addToHistory(query) {
    // Remove if already exists
    this.searchHistory = this.searchHistory.filter(q => q !== query);
    
    // Add to beginning
    this.searchHistory.unshift(query);
    
    // Keep only last 20
    if (this.searchHistory.length > 20) {
      this.searchHistory = this.searchHistory.slice(0, 20);
    }

    this.saveSearchHistory();
  }

  /**
   * Get search history
   */
  getSearchHistory() {
    return this.searchHistory;
  }

  /**
   * Clear search history
   */
  clearSearchHistory() {
    this.searchHistory = [];
    this.saveSearchHistory();
    Utils.showToast('Search history cleared', 'info');
  }

  /**
   * Get current results
   */
  getResults() {
    return this.searchResults;
  }

  /**
   * Get categories
   */
  getCategories() {
    return this.categories;
  }

  /**
   * Save search history
   */
  saveSearchHistory() {
    Utils.storage.set('linkpoint_search_history', this.searchHistory);
  }

  /**
   * Load search history
   */
  loadSearchHistory() {
    this.searchHistory = Utils.storage.get('linkpoint_search_history', []);
  }
}

// Make available globally
window.SearchManager = SearchManager;
