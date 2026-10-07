import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:linkpoint_dart/linkpoint_dart.dart';

void main() {
  testWidgets('GridStatusBanner renders region name when connected',
      (WidgetTester tester) async {
    await tester.pumpWidget(
      const MaterialApp(
        home: Scaffold(
          body: GridStatusBanner(
            isConnected: true,
            regionName: 'Aharon',
          ),
        ),
      ),
    );

    expect(find.text('Connected: Aharon'), findsOneWidget);
  });
}
