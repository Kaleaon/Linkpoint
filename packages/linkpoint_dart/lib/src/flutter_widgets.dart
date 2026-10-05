import 'package:flutter/material.dart';
import 'session.dart';

/// Flutter Widget rendering Second Life Grid Status Banner.
class GridStatusBanner extends StatelessWidget {
  final bool isConnected;
  final String regionName;

  const GridStatusBanner({
    super.key,
    required this.isConnected,
    required this.regionName,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      color: isConnected ? Colors.green.shade800 : Colors.grey.shade900,
      child: Row(
        children: [
          Icon(
            isConnected ? Icons.cloud_done : Icons.cloud_off,
            color: Colors.white,
            size: 18,
          ),
          const SizedBox(width: 8),
          Text(
            isConnected ? 'Connected: $regionName' : 'Offline',
            style: const TextStyle(
                color: Colors.white, fontWeight: FontWeight.bold),
          ),
        ],
      ),
    );
  }
}

/// Flutter Widget rendering Second Life Chat Feed.
class ChatFeedWidget extends StatelessWidget {
  final List<ChatMessage> messages;

  const ChatFeedWidget({super.key, required this.messages});

  @override
  Widget build(BuildContext context) {
    return ListView.builder(
      itemCount: messages.length,
      itemBuilder: (context, index) {
        final msg = messages[index];
        return Padding(
          padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
          child: RichText(
            text: TextSpan(
              children: [
                TextSpan(
                  text: '${msg.fromName}: ',
                  style: const TextStyle(
                      fontWeight: FontWeight.bold, color: Colors.indigoAccent),
                ),
                TextSpan(
                  text: msg.body,
                  style: const TextStyle(color: Colors.black87),
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}

/// Main Linkpoint Second Life Viewer Reactive Flutter Component.
class LinkpointViewerWidget extends StatefulWidget {
  final ViewerSession session;

  const LinkpointViewerWidget({super.key, required this.session});

  @override
  State<LinkpointViewerWidget> createState() => _LinkpointViewerWidgetState();
}

class _LinkpointViewerWidgetState extends State<LinkpointViewerWidget> {
  final List<ChatMessage> _messages = [];

  @override
  void initState() {
    super.initState();
    widget.session.events.listen((event) {
      if (event is ChatReceivedEvent) {
        setState(() {
          _messages.add(event.message);
        });
      } else if (event is SessionConnectedEvent ||
          event is SessionDisconnectedEvent) {
        setState(() {});
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final snapshot = widget.session.currentSnapshot;
    return Column(
      children: [
        GridStatusBanner(
          isConnected: widget.session.isConnected,
          regionName: snapshot?.regionName ?? 'None',
        ),
        Expanded(
          child: ChatFeedWidget(messages: _messages),
        ),
      ],
    );
  }
}
