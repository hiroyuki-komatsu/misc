package org.taiyaki.hidemu

import android.content.Context
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import org.taiyaki.hidemu.ui.theme.HIDEmulatorTheme


class MainActivity : ComponentActivity() {
    private lateinit var manager: UsbManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HIDEmulatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Keyboard(onKeyInput = ::onKeyInput)
                }
            }
        }
        manager = getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private fun onKeyInput(char: String) {
        Log.d("onKeyInput", char)

        val availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (availableDrivers.isEmpty()) {
            Log.d("onKeyInput", "no available default drivers")
            return
        }

        val driver: UsbSerialDriver = availableDrivers[0]
        val connection = manager.openDevice(driver.device)
        if (connection == null) {
            Log.d("onKeyInput", "no connection")
            return
        }

        val port = driver.ports[0] // Most devices have just one port (port 0)
        port.open(connection)
        port.setParameters(115200, 8, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)
        port.close()
        Log.d("onKeyInput", "done")
    }
}

@Composable
fun Keyboard(onKeyInput: (String) -> Unit) {
    val context = LocalContext.current
    Column {
        Button(onClick = {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
            Log.d("Button", "onClick")
            onKeyInput("a")
        }) {
            Text("Button")
        }
        Text(
            text = "Hello"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KeyboardPreview() {
    HIDEmulatorTheme {
        Keyboard(onKeyInput = {})
    }
}