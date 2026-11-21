package com.asad.tasks;

import android.app.NotificationChannel;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.Settings;
import androidx.activity.result.ActivityResult;
import com.getcapacitor.*;
import com.getcapacitor.annotation.ActivityCallback;
import com.getcapacitor.annotation.CapacitorPlugin;

@CapacitorPlugin(name = "AsadNative")
public class AsadNative extends Plugin {
  @PluginMethod
  public void pickContact(PluginCall call) {
    Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
    startActivityForResult(call, i, "pickDone");
  }

  @ActivityCallback
  private void pickDone(PluginCall call, ActivityResult r) {
    if (call == null) return;
    JSObject o = new JSObject();
    if (r.getResultCode() == android.app.Activity.RESULT_OK && r.getData() != null) {
      Cursor c = getContext().getContentResolver().query(r.getData().getData(), new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER}, null, null, null);
      if (c != null) { if (c.moveToFirst()) { o.put("name", c.getString(0)); o.put("tel", c.getString(1)); } c.close(); }
    }
    call.resolve(o);
  }

  @PluginMethod
  public void launch(PluginCall call) {
    String pkg = call.getString("pkg", "");
    Intent i = getContext().getPackageManager().getLaunchIntentForPackage(pkg);
    if (i == null) { call.reject("nf"); return; }
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    getContext().startActivity(i); call.resolve();
  }

  @PluginMethod
  public void openUri(PluginCall call) {
    try {
      String u = call.getString("url", "");
      Intent i = u.startsWith("intent:") ? Intent.parseUri(u, Intent.URI_INTENT_SCHEME) : new Intent(Intent.ACTION_VIEW, Uri.parse(u));
      i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      getContext().startActivity(i); call.resolve();
    } catch (Exception e) { call.reject("x"); }
  }

  @PluginMethod
  public void channelSettings(PluginCall call) {
    Intent i = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
    i.putExtra(Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
    i.putExtra(Settings.EXTRA_CHANNEL_ID, call.getString("id", ""));
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    getContext().startActivity(i); call.resolve();
  }

  @PluginMethod
  public void batteryOpt(PluginCall call) {
    Intent i = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:" + getContext().getPackageName()));
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    getContext().startActivity(i); call.resolve();
  }
}
