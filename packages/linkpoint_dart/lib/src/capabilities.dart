import 'llsd.dart';

/// Second Life Grid Endpoint configuration.
class GridEndpoint {
  final String name;
  final String loginUrl;
  final bool isProduction;

  const GridEndpoint({
    required this.name,
    required this.loginUrl,
    this.isProduction = true,
  });

  static const GridEndpoint secondLifeMain = GridEndpoint(
    name: 'Second Life Main Grid',
    loginUrl: 'https://login.agni.lindenlab.com/cgi-bin/login.cgi',
    isProduction: true,
  );

  static const GridEndpoint secondLifeBeta = GridEndpoint(
    name: 'Second Life Beta Grid',
    loginUrl: 'https://login.aditi.lindenlab.com/cgi-bin/login.cgi',
    isProduction: false,
  );
}

/// Capabilities Client for Second Life seed caps and HTTP endpoints.
class CapabilitiesClient {
  final String seedCapUrl;
  final Map<String, String> _capabilities = {};

  CapabilitiesClient({required this.seedCapUrl});

  bool hasCapability(String capName) => _capabilities.containsKey(capName);

  String? getCapabilityUrl(String capName) => _capabilities[capName];

  void registerCapabilities(Map<String, String> caps) {
    _capabilities.addAll(caps);
  }

  /// Parses Seed Capabilities response LLSD Map into registered capability URLs.
  void handleSeedCapabilitiesResponse(LLSDValue response) {
    if (response is LLSDMap) {
      for (final entry in response.value.entries) {
        if (entry.value is LLSDString) {
          _capabilities[entry.key] = (entry.value as LLSDString).value;
        }
      }
    }
  }

  int get count => _capabilities.length;
}
