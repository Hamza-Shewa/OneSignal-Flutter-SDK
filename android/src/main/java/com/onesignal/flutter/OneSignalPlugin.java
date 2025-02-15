package com.onesignal.flutter;

import android.annotation.SuppressLint;
import android.content.Context;

import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;

import com.onesignal.OneSignal;
import com.onesignal.common.OneSignalWrapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicBoolean;

import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.Result;

/** OneSignalPlugin */
public class OneSignalPlugin extends FlutterRegistrarResponder implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware {

  private Context context;
  private MethodChannel channel;
  private BinaryMessenger messenger;

  public OneSignalPlugin() {
  }

  private void init(Context context, BinaryMessenger messenger) {
    this.context = context;
    this.messenger = messenger;
    OneSignalWrapper.setSdkType("flutter");
    OneSignalWrapper.setSdkVersion("050209");

    channel = new MethodChannel(messenger, "OneSignal");
    channel.setMethodCallHandler(this);

    OneSignalDebug.registerWith(messenger);
    OneSignalLocation.registerWith(messenger);
    OneSignalSession.registerWith(messenger);
    OneSignalInAppMessages.registerWith(messenger);
    OneSignalUser.registerWith(messenger);
    OneSignalPushSubscription.registerWith(messenger);
    OneSignalNotifications.registerWith(messenger);
  }

  @Override
  public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
    init(binding.getApplicationContext(), binding.getBinaryMessenger());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
    onDetachedFromEngine();
  }

  private void onDetachedFromEngine() {
    // Clean up resources if needed
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.context = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {}

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {}

  @Override
  public void onDetachedFromActivity() {}

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    switch (call.method) {
      case "OneSignal#initialize":
        initWithContext(call, result);
        break;
      case "OneSignal#consentRequired":
        setConsentRequired(call, result);
        break;
      case "OneSignal#consentGiven":
        setConsentGiven(call, result);
        break;
      case "OneSignal#login":
        login(call, result);
        break;
      case "OneSignal#loginWithJWT":
        loginWithJWT(call, result);
        break;
      case "OneSignal#logout":
        logout(call, result);
        break;
      default:
        replyNotImplemented(result);
        break;
    }
  }

  private void initWithContext(MethodCall call, Result reply) {
    String appId = call.argument("appId");
    OneSignal.initWithContext(context, appId);
    replySuccess(reply, null);
  }

  private void setConsentRequired(MethodCall call, Result reply) {
    boolean required = call.argument("required");
    OneSignal.setConsentRequired(required);
    replySuccess(reply, null);
  }

  private void setConsentGiven(MethodCall call, Result reply) {
    boolean granted = call.argument("granted");
    OneSignal.setConsentGiven(granted);
    replySuccess(reply, null);
  }

  private void login(MethodCall call, Result result) {
    OneSignal.login((String) call.argument("externalId"));
    replySuccess(result, null);
  }

  private void loginWithJWT(MethodCall call, Result result) {
    OneSignal.login(
      (String) call.argument("externalId"),
      (String) call.argument("jwt")
    );
    replySuccess(result, null);
  }

  private void logout(MethodCall call, Result result) {
    OneSignal.logout();
    replySuccess(result, null);
  }
}
