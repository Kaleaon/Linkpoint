import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

void main() {
  group('ViewerSession Tests', () {
    test('Login and chat lifecycle', () async {
      final session = ViewerSession();
      final events = <ViewerEvent>[];
      session.events.listen(events.add);

      session.handleLogin(
        const LoginRequest(
          grid: 'second-life',
          loginUri: 'https://login.agni.lindenlab.com/cgi-bin/login.cgi',
          username: 'Test User',
          password: 'password',
        ),
      );

      expect(session.isConnected, true);
      expect(session.currentSnapshot?.regionName, 'Welcome Island');

      session.handleChat('Hello Second Life!');
      session.handleLogout();

      await Future.delayed(Duration.zero);

      expect(session.isConnected, false);
      expect(
        events.length,
        4,
      ); // Connecting, Connected, ChatReceived, Disconnected
    });
  });
}
