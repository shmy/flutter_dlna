package tech.shmy.flutter_dlna;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import tech.shmy.flutter_dlna.engine.DLNAContainer;
import tech.shmy.flutter_dlna.engine.MultiPointController;
import tech.shmy.flutter_dlna.service.DLNAService;

/**
 * FlutterDlnaPlugin
 */
public class FlutterDlnaPlugin implements MethodCallHandler, EventChannel.StreamHandler {
  private final ArrayList<HashMap> deviceList = new ArrayList<>();
  private final Context context;
  private final Activity activity;
  private final MultiPointController multiPointController = new MultiPointController();
  private static final String channelName = "flutter_dlna";
  private EventChannel.EventSink eventSink = null;
  private FlutterDlnaPlugin(Context context, Activity activity) {
    this.context = context;
    this.activity = activity;
  }

  /**
   * Plugin registration.
   */
  public static void registerWith(Registrar registrar) {
    final EventChannel eventChannel = new EventChannel(registrar.messenger(), channelName + "_event_channel");
    final MethodChannel methodChannel = new MethodChannel(registrar.messenger(), channelName + "_method_channel");
    FlutterDlnaPlugin instance = new FlutterDlnaPlugin(registrar.context(), registrar.activity());
    methodChannel.setMethodCallHandler(instance);
    eventChannel.setStreamHandler(instance);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("play")) {
      String url = call.argument("url");
      String uuid = call.argument("uuid");
      handlePlay(uuid, url);
    } else if (call.method.equals("search")) {
      startDLNAService();
    } else if (call.method.equals("getDevicesList")) {
      result.success(getDevicesList());
    } else {
      result.notImplemented();
    }
  }
  private ArrayList<HashMap> getDevicesList() {
    List<Device> devices = DLNAContainer.getInstance().getDevices();
    for (Device device1 : devices) {
      HashMap hm = new HashMap();
      hm.put("name", device1.getFriendlyName());
      hm.put("uuid", device1.getUDN());
      deviceList.add(hm);
    }
    return deviceList;
  }
  private void startDLNAService() {

    DLNAContainer.getInstance().setDeviceChangeListener(new DLNAContainer.DeviceChangeListener() {
      @Override
      public void onDeviceChange(Device device) {
        deviceList.clear();

        if (eventSink != null) {
          eventSink.success(getDevicesList());
        }

      }
    });
    Intent intent = new Intent(context.getApplicationContext(), DLNAService.class);
    context.startService(intent);

  }

  private Device findDevice(String uuid) {
    List<Device> devices = DLNAContainer.getInstance().getDevices();
    for (Device device1 : devices) {
      if (uuid.equalsIgnoreCase(device1.getUDN())) {
        return device1;
      }
    }
    return null;
  }

  private void handlePlay(final String uuid, final String url) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        final Device device = findDevice(uuid);
        if (device != null) {
          multiPointController.play(device, url);
        }
      }
    }).start();

  }

  @Override
  public void onListen(Object o, EventChannel.EventSink _eventSink) {
    eventSink = _eventSink;
  }

  @Override
  public void onCancel(Object o) {
    eventSink = null;
  }
}
