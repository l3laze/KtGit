package io.github.l3laze.ktgit

import android.Manifest
import android.os.Bundle
import android.util.Base64
import android.view.Window
import android.widget.Toast
import android.app.Activity
import android.webkit.WebView
import android.content.Context
import android.content.ClipData
import android.content.ClipboardManager
import android.content.pm.PackageManager

public class MainActivity : Activity() {
  private var instance: Activity = this

  protected val STORAGE_PERM_CODE: Int = 1

  public var storagePermission = false

  fun getInstance (): Activity {
    return instance
  }

  override fun onCreate (savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestWindowFeature(Window.FEATURE_NO_TITLE)
    setContentView(R.layout.activity_main)
     
    instance = this

    val myWebView: WebView = findViewById(R.id.webview)
     
    myWebView.settings.javaScriptEnabled = true
    myWebView.addJavascriptInterface(WebAppInterface(this), "Android")
     
    val unencodedHtml = "<html><body><input type=\"button\" value=\"Hai\" onClick=\"Android.showToast('Hi')\"/></body></html>";
    val encodedHtml = Base64.encodeToString(unencodedHtml.toByteArray(), Base64.NO_PADDING)
    myWebView.loadData(encodedHtml, "text/html", "base64")
    // myWebView.loadUrl("https://www.example.com")

    this.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERM_CODE);
  }

  protected fun checkPermission (permission: String, reqCode: Int) {
    when {
      this.checkSelfPermission(permission) ==
          PackageManager.PERMISSION_GRANTED -> {
        this.storagePermission = true
      } shouldShowRequestPermissionRationale(permission) -> {
        // In an educational UI, explain to the user why your app requires this
        // permission for a specific feature to behave as expected. In this UI,
        // include a "cancel" or "no thanks" button that allows the user to
        // continue using your app without granting the permission.

        // showInContextUI()
      } else -> {
        // You can directly ask for the permission.
        requestPermissions(arrayOf(permission), reqCode)
      }
    }
  }

  override fun onRequestPermissionsResult (requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    when (requestCode) {
      STORAGE_PERM_CODE -> {
        if (grantResults.isNotEmpty() &&
          grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_LONG).show()

          this.storagePermission = true
        } else {
          Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_LONG).show()

          this.storagePermission = false
        }
      }
    }
  }

  // https://stackoverflow.com/a/52113068/7665043
  protected fun Context.copyToClipboard (text: CharSequence) {
    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("label",text)
    clipboard.setPrimaryClip(clip)
  }
}
