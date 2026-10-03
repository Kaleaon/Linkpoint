import 'dart:async';
import 'math.dart';

class LoginRequest {
  final String grid;
  final String loginUri;
  final String username;
  final String password;
  final String? start;

  const LoginRequest({
    required this.grid,
    required this.loginUri,
    required this.username,
    required this.password,
    this.start,
  });
}

class SessionSnapshot {
  final String agentId;
  final String sessionId;
  final String regionName;

  const SessionSnapshot({
    required this.agentId,
    required this.sessionId,
    required this.regionName,
  });
}

class ChatMessage {
  final String id;
  final String fromId;
  final String fromName;
  final String body;
  final String timestamp;

  const ChatMessage({
    required this.id,
    required this.fromId,
    required this.fromName,
    required this.body,
    required this.timestamp,
  });
}

class SceneEntity {
  final String id;
  final String? parentId;
  final Vector3 position;
  final Quaternion rotation;

  const SceneEntity({
    required this.id,
    this.parentId,
    required this.position,
    required this.rotation,
  });
}

abstract class ViewerEvent {}

class SessionConnectingEvent extends ViewerEvent {}

class SessionConnectedEvent extends ViewerEvent {
  final SessionSnapshot snapshot;
  SessionConnectedEvent(this.snapshot);
}

class SessionDisconnectedEvent extends ViewerEvent {
  final String reason;
  SessionDisconnectedEvent(this.reason);
}

class ChatReceivedEvent extends ViewerEvent {
  final ChatMessage message;
  ChatReceivedEvent(this.message);
}

class SceneSnapshotEvent extends ViewerEvent {
  final List<SceneEntity> entities;
  SceneSnapshotEvent(this.entities);
}

/// Reactive Stream-based Viewer Session for Dart and Flutter applications.
class ViewerSession {
  final _eventController = StreamController<ViewerEvent>.broadcast();
  SessionSnapshot? _currentSnapshot;
  bool _isConnected = false;

  Stream<ViewerEvent> get events => _eventController.stream;
  bool get isConnected => _isConnected;
  SessionSnapshot? get currentSnapshot => _currentSnapshot;

  void handleLogin(LoginRequest request) {
    _eventController.add(SessionConnectingEvent());
    // Simulate successful session connection
    _currentSnapshot = SessionSnapshot(
      agentId: '00000000-0000-0000-0000-000000000001',
      sessionId: '00000000-0000-0000-0000-000000000002',
      regionName: 'Welcome Island',
    );
    _isConnected = true;
    _eventController.add(SessionConnectedEvent(_currentSnapshot!));
  }

  void handleChat(String body) {
    if (!_isConnected) return;
    final msg = ChatMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      fromId: _currentSnapshot?.agentId ?? 'system',
      fromName: 'Self',
      body: body,
      timestamp: DateTime.now().toIso8601String(),
    );
    _eventController.add(ChatReceivedEvent(msg));
  }

  void handleLogout() {
    _isConnected = false;
    _currentSnapshot = null;
    _eventController.add(SessionDisconnectedEvent('Logged out by user'));
  }

  void dispose() {
    _eventController.close();
  }
}
