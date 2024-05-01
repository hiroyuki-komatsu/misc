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

const val KEY_A = 0x04 // Keyboard a and A
const val KEY_B = 0x05 // Keyboard b and B
const val KEY_C = 0x06 // Keyboard c and C
const val KEY_D = 0x07 // Keyboard d and D
const val KEY_E = 0x08 // Keyboard e and E
const val KEY_F = 0x09 // Keyboard f and F
const val KEY_G = 0x0A // Keyboard g and G
const val KEY_H = 0x0B // Keyboard h and H
const val KEY_I = 0x0C // Keyboard i and I
const val KEY_J = 0x0D // Keyboard j and J
const val KEY_K = 0x0E // Keyboard k and K
const val KEY_L = 0x0F // Keyboard l and L
const val KEY_M = 0x10 // Keyboard m and M
const val KEY_N = 0x11 // Keyboard n and N
const val KEY_O = 0x12 // Keyboard o and O
const val KEY_P = 0x13 // Keyboard p and P
const val KEY_Q = 0x14 // Keyboard q and Q
const val KEY_R = 0x15 // Keyboard r and R
const val KEY_S = 0x16 // Keyboard s and S
const val KEY_T = 0x17 // Keyboard t and T
const val KEY_U = 0x18 // Keyboard u and U
const val KEY_V = 0x19 // Keyboard v and V
const val KEY_W = 0x1A // Keyboard w and W
const val KEY_X = 0x1B // Keyboard x and X
const val KEY_Y = 0x1C // Keyboard y and Y
const val KEY_Z = 0x1D // Keyboard z and Z
const val KEY_1 = 0x1E // Keyboard 1 and !
const val KEY_2 = 0x1F // Keyboard 2 and @
const val KEY_3 = 0x20 // Keyboard 3 and #
const val KEY_4 = 0x21 // Keyboard 4 and $
const val KEY_5 = 0x22 // Keyboard 5 and %
const val KEY_6 = 0x23 // Keyboard 6 and ^
const val KEY_7 = 0x24 // Keyboard 7 and &
const val KEY_8 = 0x25 // Keyboard 8 and *
const val KEY_9 = 0x26 // Keyboard 9 and (
const val KEY_0 = 0x27 // Keyboard 0 and )
const val KEY_ENTER = 0x28      // Keyboard Return (ENTER)
const val KEY_ESC = 0x29        // Keyboard ESCAPE
const val KEY_BACKSPACE = 0x2A  // Keyboard DELETE (Backspace)
const val KEY_TAB = 0x2B        // Keyboard Tab
const val KEY_SPACE = 0x2C      // Keyboard Spacebar
const val KEY_MINUS = 0x2D      // Keyboard - and (underscore)
const val KEY_EQUAL = 0x2E      // Keyboard = and +
const val KEY_LEFTBRACE = 0x2F  // Keyboard [ and {
const val KEY_RIGHTBRACE = 0x30 // Keyboard ] and }
const val KEY_BACKSLASH = 0x31  // Keyboard \ and ｜
const val KEY_SEMICOLON = 0x33  // Keyboard ; and :
const val KEY_APOSTROPHE = 0x34 // Keyboard ' and "
const val KEY_GRAVE = 0x35      // Keyboard Grave Accent and Tilde
const val KEY_COMMA = 0x36      // Keyboard, and <
const val KEY_DOT = 0x37        // Keyboard . and >
const val KEY_SLASH = 0x38      // Keyboard / and
const val KEY_CAPSLOCK = 0x39   // Keyboard Caps Lock
const val KEY_F1 = 0x3A         // Keyboard F1
const val KEY_F2 = 0x3B         // Keyboard F2
const val KEY_F3 = 0x3C         // Keyboard F3
const val KEY_F4 = 0x3D         // Keyboard F4
const val KEY_F5 = 0x3E         // Keyboard F5
const val KEY_F6 = 0x3F         // Keyboard F6
const val KEY_F7 = 0x40         // Keyboard F7
const val KEY_F8 = 0x41         // Keyboard F8
const val KEY_F9 = 0x42         // Keyboard F9
const val KEY_F10 = 0x43        // Keyboard F10
const val KEY_F11 = 0x44        // Keyboard F11
const val KEY_F12 = 0x45        // Keyboard F12

fun getKeyCode(char: String): Pair<Int, Int> {
    return when (char) {
        "a" -> {
            Pair(0x00, KEY_A)
        }

        "b" -> {
            Pair(0x00, KEY_B)
        }

        "c" -> {
            Pair(0x00, KEY_C)
        }

        "d" -> {
            Pair(0x00, KEY_D)
        }

        "e" -> {
            Pair(0x00, KEY_E)
        }

        "f" -> {
            Pair(0x00, KEY_F)
        }

        "g" -> {
            Pair(0x00, KEY_G)
        }

        "h" -> {
            Pair(0x00, KEY_H)
        }

        "i" -> {
            Pair(0x00, KEY_I)
        }

        "j" -> {
            Pair(0x00, KEY_J)
        }

        "k" -> {
            Pair(0x00, KEY_K)
        }

        "l" -> {
            Pair(0x00, KEY_L)
        }

        "m" -> {
            Pair(0x00, KEY_M)
        }

        "n" -> {
            Pair(0x00, KEY_N)
        }

        "o" -> {
            Pair(0x00, KEY_O)
        }

        "p" -> {
            Pair(0x00, KEY_P)
        }

        "q" -> {
            Pair(0x00, KEY_Q)
        }

        "r" -> {
            Pair(0x00, KEY_R)
        }

        "s" -> {
            Pair(0x00, KEY_S)
        }

        "t" -> {
            Pair(0x00, KEY_T)
        }

        "u" -> {
            Pair(0x00, KEY_U)
        }

        "v" -> {
            Pair(0x00, KEY_V)
        }

        "w" -> {
            Pair(0x00, KEY_W)
        }

        "x" -> {
            Pair(0x00, KEY_X)
        }

        "y" -> {
            Pair(0x00, KEY_Y)
        }

        "z" -> {
            Pair(0x00, KEY_Z)
        }

        "A" -> {
            Pair(0x02, KEY_A)
        }

        "B" -> {
            Pair(0x02, KEY_B)
        }

        "C" -> {
            Pair(0x02, KEY_C)
        }

        "D" -> {
            Pair(0x02, KEY_D)
        }

        "E" -> {
            Pair(0x02, KEY_E)
        }

        "F" -> {
            Pair(0x02, KEY_F)
        }

        "G" -> {
            Pair(0x02, KEY_G)
        }

        "H" -> {
            Pair(0x02, KEY_H)
        }

        "I" -> {
            Pair(0x02, KEY_I)
        }

        "J" -> {
            Pair(0x02, KEY_J)
        }

        "K" -> {
            Pair(0x02, KEY_K)
        }

        "L" -> {
            Pair(0x02, KEY_L)
        }

        "M" -> {
            Pair(0x02, KEY_M)
        }

        "N" -> {
            Pair(0x02, KEY_N)
        }

        "O" -> {
            Pair(0x02, KEY_O)
        }

        "P" -> {
            Pair(0x02, KEY_P)
        }

        "Q" -> {
            Pair(0x02, KEY_Q)
        }

        "R" -> {
            Pair(0x02, KEY_R)
        }

        "S" -> {
            Pair(0x02, KEY_S)
        }

        "T" -> {
            Pair(0x02, KEY_T)
        }

        "U" -> {
            Pair(0x02, KEY_U)
        }

        "V" -> {
            Pair(0x02, KEY_V)
        }

        "W" -> {
            Pair(0x02, KEY_W)
        }

        "X" -> {
            Pair(0x02, KEY_X)
        }

        "Y" -> {
            Pair(0x02, KEY_Y)
        }

        "Z" -> {
            Pair(0x02, KEY_Z)
        }

        "1" -> {
            Pair(0x00, KEY_1)
        }

        "2" -> {
            Pair(0x00, KEY_2)
        }

        "3" -> {
            Pair(0x00, KEY_3)
        }

        "4" -> {
            Pair(0x00, KEY_4)
        }

        "5" -> {
            Pair(0x00, KEY_5)
        }

        "6" -> {
            Pair(0x00, KEY_6)
        }

        "7" -> {
            Pair(0x00, KEY_7)
        }

        "8" -> {
            Pair(0x00, KEY_8)
        }

        "9" -> {
            Pair(0x00, KEY_9)
        }

        "0" -> {
            Pair(0x00, KEY_0)
        }

        "!" -> {
            Pair(0x02, KEY_1)
        }

        "@" -> {
            Pair(0x02, KEY_2)
        }

        "#" -> {
            Pair(0x02, KEY_3)
        }

        "$" -> {
            Pair(0x02, KEY_4)
        }

        "%" -> {
            Pair(0x02, KEY_5)
        }

        "^" -> {
            Pair(0x02, KEY_6)
        }

        "&" -> {
            Pair(0x02, KEY_7)
        }

        "*" -> {
            Pair(0x02, KEY_8)
        }

        "(" -> {
            Pair(0x02, KEY_9)
        }

        ")" -> {
            Pair(0x02, KEY_0)
        }

        "ENTER" -> {
            Pair(0x00, KEY_ENTER)
        }

        "ESC" -> {
            Pair(0x00, KEY_ESC)
        }

        "BACKSPACE" -> {
            Pair(0x00, KEY_BACKSPACE)
        }

        "TAB" -> {
            Pair(0x00, KEY_TAB)
        }

        "SPACE" -> {
            Pair(0x00, KEY_SPACE)
        }

        "-" -> {
            Pair(0x00, KEY_MINUS)
        }

        "=" -> {
            Pair(0x00, KEY_EQUAL)
        }

        "[" -> {
            Pair(0x00, KEY_LEFTBRACE)
        }

        "]" -> {
            Pair(0x00, KEY_RIGHTBRACE)
        }

        "\\" -> {
            Pair(0x00, KEY_BACKSLASH)
        }

        ";" -> {
            Pair(0x00, KEY_SEMICOLON)
        }

        "'" -> {
            Pair(0x00, KEY_APOSTROPHE)
        }

        "`" -> {
            Pair(0x00, KEY_GRAVE)
        }

        "," -> {
            Pair(0x00, KEY_COMMA)
        }

        "." -> {
            Pair(0x00, KEY_DOT)
        }

        "/" -> {
            Pair(0x00, KEY_SLASH)
        }

        "_" -> {
            Pair(0x02, KEY_MINUS)
        }

        "+" -> {
            Pair(0x02, KEY_EQUAL)
        }

        "{" -> {
            Pair(0x02, KEY_LEFTBRACE)
        }

        "}" -> {
            Pair(0x02, KEY_RIGHTBRACE)
        }

        "|" -> {
            Pair(0x02, KEY_BACKSLASH)
        }

        ":" -> {
            Pair(0x02, KEY_SEMICOLON)
        }

        "\"" -> {
            Pair(0x02, KEY_APOSTROPHE)
        }

        "~" -> {
            Pair(0x02, KEY_GRAVE)
        }

        "<" -> {
            Pair(0x02, KEY_COMMA)
        }

        ">" -> {
            Pair(0x02, KEY_DOT)
        }

        "?" -> {
            Pair(0x02, KEY_SLASH)
        }

        "CAPSLOCK" -> {
            Pair(0x00, KEY_CAPSLOCK)
        }

        "F1" -> {
            Pair(0x00, KEY_F1)
        }

        "F2" -> {
            Pair(0x00, KEY_F2)
        }

        "F3" -> {
            Pair(0x00, KEY_F3)
        }

        "F4" -> {
            Pair(0x00, KEY_F4)
        }

        "F5" -> {
            Pair(0x00, KEY_F5)
        }

        "F6" -> {
            Pair(0x00, KEY_F6)
        }

        "F7" -> {
            Pair(0x00, KEY_F7)
        }

        "F8" -> {
            Pair(0x00, KEY_F8)
        }

        "F9" -> {
            Pair(0x00, KEY_F9)
        }

        "F10" -> {
            Pair(0x00, KEY_F10)
        }

        "F11" -> {
            Pair(0x00, KEY_F11)
        }

        "F12" -> {
            Pair(0x00, KEY_F12)
        }

        else -> {
            Pair(0, 0)
        }
    }
}

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

    private fun createPacketForKeyDown(key: String): ByteArray {
        val (modifiers, hidCode) = getKeyCode(key)
        val packet = byteArrayOf(
            0x57, 0xAB.toByte(), 0x00, 0x02, 0x08, modifiers.toByte(),
            0x00, hidCode.toByte(), 0x00, 0x00, 0x00, 0x00, 0x00, 0x00
        )
        packet[packet.size - 1] = getParity(packet)
        return packet
    }

    private fun onKeyInput(keyChar: String): String {
        var logs: Array<String> = arrayOf("onKeyInput: $keyChar", "")
        Log.d("onKeyInput", keyChar)

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

        val keyDownPacket: ByteArray = createPacketForKeyDown(keyChar)
        logs += sendKey(port, keyDownPacket)

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
                val log = onKeyInput("BACKSPACE")
                setMessage(log)
            }) {
                Text("BACKSPACE")
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