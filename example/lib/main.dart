import 'package:flutter/material.dart';
import 'package:flutter_dlna/flutter_dlna.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<dynamic> _devices = [];
  @override
  void initState() {
    super.initState();
    FlutterDlna.subscribe((devices) {
      setState(() {
        _devices = devices;
      });
    });
    FlutterDlna.search();
  }


  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: _devices.map((device) {
            return MaterialButton(
              onPressed: () {
                FlutterDlna.play(device['uuid'], 'https://v3.juhui600.com/20190602/ifcRudnw/index.m3u8');
              },
              child: Text(device['name']),
            );
          }).toList(),
        ),
      ),
    );
  }
}
