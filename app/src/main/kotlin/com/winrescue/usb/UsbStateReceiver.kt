package com.winrescue.usb

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UsbStateReceiver : BroadcastReceiver() {

    var onUsbStateChanged: ((Boolean) -> Unit)? = null

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.hardware.usb.action.USB_STATE") {
            val connected = intent.getBooleanExtra("connected", false)
            onUsbStateChanged?.invoke(connected)
        }
    }
}
