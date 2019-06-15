import 'dart:async';

import 'package:flutter/services.dart';

class FlutterDlna {
  static const String channelName = "flutter_dlna";
  static const MethodChannel _methodChannel =
      const MethodChannel(channelName + '_method_channel');
  static const EventChannel _eventChannel =
      const EventChannel(channelName + '_event_channel');

  static StreamSubscription eventSubscription;

  static subscribe(callback) {
    eventSubscription =
        _eventChannel.receiveBroadcastStream().listen((dynamic data) {
      callback(data);
    });
  }

  static unsubscribe() {
    if (eventSubscription != null) {
      eventSubscription.cancel();
      eventSubscription = null;
    }
  }

  static void search() async {
    await _methodChannel.invokeMethod('search');
  }
  static Future<List<dynamic>> get devices async {
    try {
      final List<dynamic> devices =
      await _methodChannel.invokeMethod('getDevicesList');
      return devices;
    } on Exception catch (_) {
      return [];
    }
  }
  static void play(String uuid, String url) async {
    await _methodChannel.invokeMethod('play', {
      "uuid": uuid,
      "url": url,
    });
  }
  static void stop() async {
    await _methodChannel.invokeMethod('stop');
  }
}
