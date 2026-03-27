package com.winrescue.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.winrescue.ui.theme.UsbConnected
import com.winrescue.ui.theme.UsbDisconnected
import com.winrescue.ui.theme.Warning
import com.winrescue.ui.theme.WinRescueTheme
import com.winrescue.usb.UsbConnectionState

@Composable
fun UsbStatusBar(
    connectionState: UsbConnectionState,
    modifier: Modifier = Modifier
) {
    val (statusColor, statusText) = when (connectionState) {
        UsbConnectionState.CONNECTED_HID_READY -> UsbConnected to "USB: Connect\u00e9"
        UsbConnectionState.CONNECTED_NO_HID -> Warning to "USB: Connect\u00e9 (HID non pr\u00eat)"
        UsbConnectionState.DISCONNECTED -> UsbDisconnected to "USB: Non connect\u00e9"
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Usb,
            contentDescription = null,
            tint = statusColor,
            modifier = Modifier.size(20.dp)
        )

        Text(
            text = statusText,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(statusColor)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun UsbStatusBarConnectedPreview() {
    WinRescueTheme {
        UsbStatusBar(connectionState = UsbConnectionState.CONNECTED_HID_READY)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun UsbStatusBarNoHidPreview() {
    WinRescueTheme {
        UsbStatusBar(connectionState = UsbConnectionState.CONNECTED_NO_HID)
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF080C18)
@Composable
private fun UsbStatusBarDisconnectedPreview() {
    WinRescueTheme {
        UsbStatusBar(connectionState = UsbConnectionState.DISCONNECTED)
    }
}
