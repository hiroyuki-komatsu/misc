package org.taiyaki.hidemu

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import org.taiyaki.hidemu.ui.theme.HIDEmulatorTheme

const val VENDOR_ID: Int = 0x1A86
const val PRODUCT_ID: Int = 0x7523
const val WRITE_WAIT_MILLIS: Int = 20
const val READ_WAIT_MILLIS: Int = 20

class MainActivity : ComponentActivity() {
    private lateinit var manager: UsbManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HIDEmulatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Keyboard(onKeyInput = ::onKeyInput)
                }
            }
        }
        manager = getSystemService(Context.USB_SERVICE) as UsbManager
    }

    private fun getDeviceInfo(device: UsbDevice): String {
        return String.format(
            "%s(%s): VID=0x%X, PID=0x%X",
            device.productName,
            device.deviceName,
            device.vendorId,
            device.productId
        )
    }

    private fun getPacketInfo(packet: ByteArray): String {
        return packet.joinToString(", ") { String.format("%02X", it) }
    }

    private fun checkReadPacket(packet: ByteArray): String {
        if (packet.size != 7) {
            return "invalid size"
        }
        val code: Byte = packet[5]
        return when (code) {
            0x00.toByte() -> {
                "success"
            }

            0xE1.toByte() -> {
                "timeout error"
            }

            0xE2.toByte() -> {
                "header bytes error"
            }

            0xE3.toByte() -> {
                "command code error"
            }

            0xE4.toByte() -> {
                "invalid parity error"
            }

            0xE5.toByte() -> {
                "parameter error"
            }

            0xE6.toByte() -> {
                "operation error"
            }

            else -> {
                "unknown"
            }
        }
    }

    private fun sendKeyDown(port: UsbSerialPort, keyDown: ByteArray): String {
        var logs: Array<String> = arrayOf("")
        val buffer1 = ByteArray(size = 16)

        logs += "# key down"
        port.write(keyDown, WRITE_WAIT_MILLIS)
        logs += "sent: " + getPacketInfo(keyDown)
        val len1 = port.read(buffer1, READ_WAIT_MILLIS)
        val readPacket1 = buffer1.sliceArray(0..<len1)
        logs += "read: " + getPacketInfo(readPacket1)
        logs += checkReadPacket(readPacket1) + "\n"

        return logs.joinToString(separator = "\n")
    }

    private fun sendKeyUp(port: UsbSerialPort): String {
        var logs: Array<String> = arrayOf("")
        val keyUp = byteArrayOf(
            0x57, 0xAB.toByte(), 0, 0x02, 0x08, 0x00, 0, 0x00, 0, 0, 0, 0, 0, 0x0C
        )
        val buffer2 = ByteArray(size = 16)

        logs += "# key up"
        port.write(keyUp, WRITE_WAIT_MILLIS)
        logs += "sent: " + getPacketInfo(keyUp)
        val len2 = port.read(buffer2, READ_WAIT_MILLIS)
        val readPacket2 = buffer2.sliceArray(0..<len2)
        logs += "read: " + getPacketInfo(readPacket2)
        logs += checkReadPacket(readPacket2) + "\n"
        return logs.joinToString(separator = "\n")
    }

    private fun sendKey(port: UsbSerialPort, keyDown: ByteArray): String {
        var logs: Array<String> = arrayOf("")
        val keyUp = byteArrayOf(
            0x57, 0xAB.toByte(), 0, 0x02, 0x08, 0x00, 0, 0x00, 0, 0, 0, 0, 0, 0x0C
        )
        val buffer1 = ByteArray(size = 16)
        val buffer2 = ByteArray(size = 16)

        logs += "# key down"
        port.write(keyDown, WRITE_WAIT_MILLIS)
        logs += "sent: " + getPacketInfo(keyDown)
        val len1 = port.read(buffer1, READ_WAIT_MILLIS)
        val readPacket1 = buffer1.sliceArray(0..<len1)
        logs += "read: " + getPacketInfo(readPacket1)
        logs += checkReadPacket(readPacket1) + "\n"

        logs += "# key up"
        port.write(keyUp, WRITE_WAIT_MILLIS)
        logs += "sent: " + getPacketInfo(keyUp)
        val len2 = port.read(buffer2, READ_WAIT_MILLIS)
        val readPacket2 = buffer2.sliceArray(0..<len2)
        logs += "read: " + getPacketInfo(readPacket2)
        logs += checkReadPacket(readPacket2) + "\n"
        return logs.joinToString(separator = "\n")
    }

    private fun getParity(packet: ByteArray): Byte {
        var sum = 0
        for (byte in packet) {
            sum += (byte.toInt() and 0xFF)
        }
        return (sum and 0xFF).toByte()
    }

    private fun createPacketForKeyDown(modifiers: Byte, hidCode: Byte): ByteArray {
        val packet = byteArrayOf(
            0x57, 0xAB.toByte(), 0x00, 0x02, 0x08, modifiers,
            0x00, hidCode, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        )
        packet[packet.size - 1] = getParity(packet)
        return packet
    }

    private fun onKeyInput(char: String): String {
        var logs: Array<String> = arrayOf("onKeyInput: $char", "")
        Log.d("onKeyInput", char)

        logs += "# connected devices:"
        for (item in manager.deviceList) {
            logs += getDeviceInfo(item.value)
        }
        logs += ""

        val drivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager)
        if (drivers.isEmpty()) {
            val msg = "no available default drivers"
            Log.d("onKeyInput", msg)
            logs += msg
            return logs.joinToString(separator = "\n")
        }

        var driver: UsbSerialDriver? = null

        logs += "# available drivers:"
        for (candidate in drivers) {
            val device: UsbDevice = candidate.device
            logs += getDeviceInfo(device)
            if (device.vendorId == VENDOR_ID && device.productId == PRODUCT_ID) {
                driver = candidate
            }
        }
        logs += ""

        if (driver == null) {
            val msg = "no target drivers"
            Log.d("onKeyInput", msg)
            logs += msg
            return logs.joinToString(separator = "\n")
        }

        if (!manager.hasPermission(driver.device)) {
            val msg = "no permission"
            Log.d("onKeyInput", msg)
            logs += msg
            return logs.joinToString(separator = "\n")
        }

        val connection = manager.openDevice(driver.device)
        if (connection == null) {
            val msg = "no connection"
            Log.d("onKeyInput", msg)
            logs += msg
            return logs.joinToString(separator = "\n")
        }

        val port = driver.ports[0]
        port.open(connection)

        val baudRate = 9600
        val dataBits = 8
        port.setParameters(baudRate, dataBits, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

        when (char) {
            "A" -> {
                // Shift + a
                val keyDownShift = createPacketForKeyDown(0x02, 0x00)
                logs += sendKeyDown(port, keyDownShift)
                val keyDownA = createPacketForKeyDown(0x02, 0x04)
                logs += sendKeyDown(port, keyDownA)
                logs += sendKeyUp(port)
            }

            "a" -> {
                // a
                val keyDownA = createPacketForKeyDown(0x00, 0x04)
                logs += sendKey(port, keyDownA)
            }

            else -> {
                // Backspace (0x2A)
                val keyDownBs = createPacketForKeyDown(0x00, 0x2A)
                logs += sendKey(port, keyDownBs)
            }
        }

        port.close()
        val msg = "done"
        Log.d("onKeyInput", msg)
        logs += msg
        return logs.joinToString(separator = "\n")
    }
}

@Composable
fun Keyboard(onKeyInput: (String) -> String) {
    val context = LocalContext.current
    val (message, setMessage) = remember { mutableStateOf("") }
    Column {
        Row {
            Button(onClick = {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
                Log.d("Button", "onClick(a)")
                val log = onKeyInput("a")
                setMessage(log)
            }) {
                Text("a")
            }
            Button(onClick = {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
                Log.d("Button", "onClick(A)")
                val log = onKeyInput("A")
                setMessage(log)
            }) {
                Text("A")
            }
            Button(onClick = {
                Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
                Log.d("Button", "onClick(BS)")
                val log = onKeyInput("BS")
                setMessage(log)
            }) {
                Text("BS")
            }
        }
        Text(
            text = message
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KeyboardPreview() {
    HIDEmulatorTheme {
        Keyboard(onKeyInput = { "" })
    }
}