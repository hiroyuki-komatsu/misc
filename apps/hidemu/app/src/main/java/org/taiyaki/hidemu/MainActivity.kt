package org.taiyaki.hidemu

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoho.android.usbserial.driver.UsbSerialDriver
import com.hoho.android.usbserial.driver.UsbSerialPort
import com.hoho.android.usbserial.driver.UsbSerialProber
import org.taiyaki.hidemu.ui.theme.HIDEmulatorTheme
import kotlin.math.max

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
const val KEY_RIGHT = 0x4F  // Keyboard RightArrow
const val KEY_LEFT = 0x50  // Keyboard LeftArrow
const val KEY_DOWN = 0x51  // Keyboard DownArrow
const val KEY_UP = 0x52  // Keyboard UpArrow
const val KEY_LEFTCTRL = 0xE0  // Keyboard LeftControl
const val KEY_LEFTSHIFT = 0xE1  // Keyboard LeftShift
const val KEY_RIGHTCTRL = 0xE4  // Keyboard RightControl
const val KEY_RIGHTSHIFT = 0xE5 // Keyboard LeftShift
const val KEY_HANGEUL = 0x90 // Keyboard LANG1
const val KEY_HANJA = 0x91 // Keyboard LANG2
const val KEY_LANG1 = 0x90 // Keyboard LANG1
const val KEY_LANG2 = 0x91 // Keyboard LANG2

val KEYCODE_MAP: Map<String, Pair<Int, Int>> = mapOf(
    "a" to Pair(0x00, KEY_A),
    "b" to Pair(0x00, KEY_B),
    "c" to Pair(0x00, KEY_C),
    "d" to Pair(0x00, KEY_D),
    "e" to Pair(0x00, KEY_E),
    "f" to Pair(0x00, KEY_F),
    "g" to Pair(0x00, KEY_G),
    "h" to Pair(0x00, KEY_H),
    "i" to Pair(0x00, KEY_I),
    "j" to Pair(0x00, KEY_J),
    "k" to Pair(0x00, KEY_K),
    "l" to Pair(0x00, KEY_L),
    "m" to Pair(0x00, KEY_M),
    "n" to Pair(0x00, KEY_N),
    "o" to Pair(0x00, KEY_O),
    "p" to Pair(0x00, KEY_P),
    "q" to Pair(0x00, KEY_Q),
    "r" to Pair(0x00, KEY_R),
    "s" to Pair(0x00, KEY_S),
    "t" to Pair(0x00, KEY_T),
    "u" to Pair(0x00, KEY_U),
    "v" to Pair(0x00, KEY_V),
    "w" to Pair(0x00, KEY_W),
    "x" to Pair(0x00, KEY_X),
    "y" to Pair(0x00, KEY_Y),
    "z" to Pair(0x00, KEY_Z),
    "A" to Pair(0x02, KEY_A),
    "B" to Pair(0x02, KEY_B),
    "C" to Pair(0x02, KEY_C),
    "D" to Pair(0x02, KEY_D),
    "E" to Pair(0x02, KEY_E),
    "F" to Pair(0x02, KEY_F),
    "G" to Pair(0x02, KEY_G),
    "H" to Pair(0x02, KEY_H),
    "I" to Pair(0x02, KEY_I),
    "J" to Pair(0x02, KEY_J),
    "K" to Pair(0x02, KEY_K),
    "L" to Pair(0x02, KEY_L),
    "M" to Pair(0x02, KEY_M),
    "N" to Pair(0x02, KEY_N),
    "O" to Pair(0x02, KEY_O),
    "P" to Pair(0x02, KEY_P),
    "Q" to Pair(0x02, KEY_Q),
    "R" to Pair(0x02, KEY_R),
    "S" to Pair(0x02, KEY_S),
    "T" to Pair(0x02, KEY_T),
    "U" to Pair(0x02, KEY_U),
    "V" to Pair(0x02, KEY_V),
    "W" to Pair(0x02, KEY_W),
    "X" to Pair(0x02, KEY_X),
    "Y" to Pair(0x02, KEY_Y),
    "Z" to Pair(0x02, KEY_Z),
    "1" to Pair(0x00, KEY_1),
    "2" to Pair(0x00, KEY_2),
    "3" to Pair(0x00, KEY_3),
    "4" to Pair(0x00, KEY_4),
    "5" to Pair(0x00, KEY_5),
    "6" to Pair(0x00, KEY_6),
    "7" to Pair(0x00, KEY_7),
    "8" to Pair(0x00, KEY_8),
    "9" to Pair(0x00, KEY_9),
    "0" to Pair(0x00, KEY_0),
    "!" to Pair(0x02, KEY_1),
    "@" to Pair(0x02, KEY_2),
    "#" to Pair(0x02, KEY_3),
    "$" to Pair(0x02, KEY_4),
    "%" to Pair(0x02, KEY_5),
    "^" to Pair(0x02, KEY_6),
    "&" to Pair(0x02, KEY_7),
    "*" to Pair(0x02, KEY_8),
    "(" to Pair(0x02, KEY_9),
    ")" to Pair(0x02, KEY_0),
    "ENTER" to Pair(0x00, KEY_ENTER),
    "ESC" to Pair(0x00, KEY_ESC),
    "BACKSPACE" to Pair(0x00, KEY_BACKSPACE),
    "TAB" to Pair(0x00, KEY_TAB),
    "SPACE" to Pair(0x00, KEY_SPACE),
    "-" to Pair(0x00, KEY_MINUS),
    "=" to Pair(0x00, KEY_EQUAL),
    "[" to Pair(0x00, KEY_LEFTBRACE),
    "]" to Pair(0x00, KEY_RIGHTBRACE),
    "\\" to Pair(0x00, KEY_BACKSLASH),
    ";" to Pair(0x00, KEY_SEMICOLON),
    "'" to Pair(0x00, KEY_APOSTROPHE),
    "`" to Pair(0x00, KEY_GRAVE),
    "," to Pair(0x00, KEY_COMMA),
    "." to Pair(0x00, KEY_DOT),
    "/" to Pair(0x00, KEY_SLASH),
    "_" to Pair(0x02, KEY_MINUS),
    "+" to Pair(0x02, KEY_EQUAL),
    "{" to Pair(0x02, KEY_LEFTBRACE),
    "}" to Pair(0x02, KEY_RIGHTBRACE),
    "|" to Pair(0x02, KEY_BACKSLASH),
    ":" to Pair(0x02, KEY_SEMICOLON),
    "\"" to Pair(0x02, KEY_APOSTROPHE),
    "~" to Pair(0x02, KEY_GRAVE),
    "<" to Pair(0x02, KEY_COMMA),
    ">" to Pair(0x02, KEY_DOT),
    "?" to Pair(0x02, KEY_SLASH),
    "CAPSLOCK" to Pair(0x00, KEY_CAPSLOCK),
    "F1" to Pair(0x00, KEY_F1),
    "F2" to Pair(0x00, KEY_F2),
    "F3" to Pair(0x00, KEY_F3),
    "F4" to Pair(0x00, KEY_F4),
    "F5" to Pair(0x00, KEY_F5),
    "F6" to Pair(0x00, KEY_F6),
    "F7" to Pair(0x00, KEY_F7),
    "F8" to Pair(0x00, KEY_F8),
    "F9" to Pair(0x00, KEY_F9),
    "F10" to Pair(0x00, KEY_F10),
    "F11" to Pair(0x00, KEY_F11),
    "F12" to Pair(0x00, KEY_F12),
    "RIGHT" to Pair(0x00, KEY_RIGHT),
    "LEFT" to Pair(0x00, KEY_LEFT),
    "DOWN" to Pair(0x00, KEY_DOWN),
    "UP" to Pair(0x00, KEY_UP),
    "LEFTCTRL" to Pair(0x00, KEY_LEFTCTRL),
    "LEFTSHIFT" to Pair(0x00, KEY_LEFTSHIFT),
    "RIGHTCTRL" to Pair(0x00, KEY_RIGHTCTRL),
    "RIGHTSHIFT" to Pair(0x00, KEY_RIGHTSHIFT),
    "LANG1" to Pair(0x00, KEY_LANG1),
    "LANG2" to Pair(0x00, KEY_LANG2),
)

val CODEMAP: Map<Int, String> = mapOf(
    0x00 to "success",
    0xE1 to "timeout error",
    0xE2 to "header bytes error",
    0xE3 to "command code error",
    0xE4 to "invalid parity error",
    0xE5 to "parameter error",
    0xE6 to "operation error",
)


fun getKeyCode(char: String): Pair<Int, Int> {
    return KEYCODE_MAP[char] ?: Pair(0x00, 0x00)
}

private class Event {
    var type: String = ""
    var key: String = ""
    var dX: Int = 0
    var dY: Int = 0
    var left: Boolean = false
    fun setKeyEvent(key: String): Event {
        type = "key"
        this.key = key
        return this
    }

    fun setMouseEvent(dX: Int = 0, dY: Int = 0, left: Boolean = false): Event {
        type = "mouse"
        this.dX = dX
        this.dY = dY
        this.left = left
        return this
    }
}

private class DebugLog {
    private var count: Int = 0
    private var logs: MutableList<String> = mutableListOf("# count: $count")

    operator fun plusAssign(log: String) {
        logs += log
    }

    fun last(): String {
        return logs.last()
    }

    fun consume(): String {
        val log = logs.joinToString(separator = "\n")
        logs.clear()
        count++
        logs += "# count: $count"
        return log
    }
}

private var logs = DebugLog()

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
                    MainView(onEvent = ::onEvent)
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
        val code: Int = packet[5].toInt()
        return CODEMAP[code] ?: "unknown"
    }

    private fun sendKeyDown(port: UsbSerialPort, keyDown: ByteArray) {
        val buffer1 = ByteArray(size = 16)

        logs += "# key down"
        port.write(keyDown, WRITE_WAIT_MILLIS)
        logs += "sent: " + getPacketInfo(keyDown)
        val len1 = port.read(buffer1, READ_WAIT_MILLIS)
        val readPacket1 = buffer1.sliceArray(0..<len1)
        logs += "read: " + getPacketInfo(readPacket1)
        logs += checkReadPacket(readPacket1) + "\n"
    }

    private fun sendKeyUp(port: UsbSerialPort) {
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
    }

    private fun sendKey(port: UsbSerialPort, keyDown: ByteArray) {
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
    }

    private fun moveMouse(port: UsbSerialPort, packet: ByteArray) {
        val buffer = ByteArray(size = 16)

        logs += "# move mouse"
        port.write(packet, WRITE_WAIT_MILLIS)
        logs += "sent: " + getPacketInfo(packet)
        val len = port.read(buffer, READ_WAIT_MILLIS)
        val readPacket = buffer.sliceArray(0..<len)
        logs += "read: " + getPacketInfo(readPacket)
        logs += checkReadPacket(readPacket) + "\n"
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
            0x57,
            0xAB.toByte(),
            0x00,
            0x02,  // CMD_SEND_KB_GENERAL_DATA
            0x08,  // Length of data
            modifiers.toByte(),  // [RWin, RAlt, RShift, RCtrl, LWin, LAlt, LShift, LCtrl]
            0x00,  // Always 0x00
            hidCode.toByte(),  // USB HID Keycode1
            0x00,  // USB HID Keycode2
            0x00,  // USB HID Keycode3
            0x00,  // USB HID Keycode4
            0x00,  // USB HID Keycode5
            0x00,  // USB HID Keycode6
            0x00,  // Parity: Sum of bytes (mod 0xFF)
        )
        packet[packet.size - 1] = getParity(packet)
        return packet
    }

    private fun createPacketForMouseMove(x: Int, y: Int, left: Boolean): ByteArray {
        val packet = byteArrayOf(
            0x57,
            0xAB.toByte(),
            0x00,
            0x05,  // CMD_SEND_MS_REL_DATA
            0x05,  // Length of data
            0x01,  // Always 0x01
            (if (left) 0x01 else 0x00),  // [0..0, mid_btn, r_btn, l_btn]
            x.toByte(),  // Delta of x
            y.toByte(),  // Delta of y
            0x00,  // Delta of wheel
            0x00,  // Parity: Sum of bytes (mod 0xFF)
        )
        packet[packet.size - 1] = getParity(packet)
        return packet
    }

    private fun openPort(): UsbSerialPort? {
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
            return null
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
            return null
        }

        if (!manager.hasPermission(driver.device)) {
            val msg = "no permission"
            Log.d("onKeyInput", msg)
            logs += msg
            return null
        }

        val connection = manager.openDevice(driver.device)
        if (connection == null) {
            val msg = "no connection"
            Log.d("onKeyInput", msg)
            logs += msg
            return null
        }

        val port = driver.ports[0]
        port.open(connection)
        return port
    }

    private fun onEvent(event: Event) {
        logs += "onEvent: ${event.type}"
        when (event.type) {
            "key" -> {
                onKeyInput(event.key)
            }

            "mouse" -> {
                onMouseMove(event.dX, event.dY, event.left)
            }

            else -> {
                logs += "unknown event"
                Log.d("onEvent", logs.last())
            }
        }
    }

    private fun onKeyInput(keyChar: String) {
        logs += "onKeyInput: $keyChar"
        Log.d("onKeyInput", keyChar)

        val port = openPort()
        if (port == null) {
            logs += "port is null"
            return
        }

        val baudRate = 9600
        val dataBits = 8
        port.setParameters(baudRate, dataBits, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

        val keyDownPacket: ByteArray = createPacketForKeyDown(keyChar)
        sendKey(port, keyDownPacket)

        port.close()
        logs += "done"
        Log.d("onKeyInput", logs.last())
    }

    private fun onMouseMove(x: Int, y: Int, left: Boolean = false) {
        logs += "onMouseMove: ($x, $y)"
        Log.d("onMouseMove", logs.last())

        val port = openPort()
        if (port == null) {
            logs += "port is null"
            return
        }

        val baudRate = 9600
        val dataBits = 8
        port.setParameters(baudRate, dataBits, UsbSerialPort.STOPBITS_1, UsbSerialPort.PARITY_NONE)

        val packet: ByteArray = createPacketForMouseMove(x, y, left)
        moveMouse(port, packet)

        if (left) {
            // Reset click state
            val resetPacket: ByteArray = createPacketForMouseMove(0, 0, false)
            moveMouse(port, resetPacket)
        }

        port.close()
        logs += "done"
        Log.d("onMouseMove", logs.last())
    }
}

typealias LayoutMap = Map<String, List<String>>

val LAYOUT_MAP: LayoutMap = mapOf(
    // Row of numbers
    "POS_ESC" to listOf("Esc", "ESC"),
    "POS_1" to listOf("1", "1"),
    "POS_2" to listOf("2", "2"),
    "POS_3" to listOf("3", "3"),
    "POS_4" to listOf("4", "4"),
    "POS_5" to listOf("5", "5"),
    "POS_6" to listOf("6", "6"),
    "POS_7" to listOf("7", "7"),
    "POS_8" to listOf("8", "8"),
    "POS_9" to listOf("9", "9"),
    "POS_0" to listOf("0", "0"),
    "POS_MINUS" to listOf("-", "-"),
    "POS_EQUAL" to listOf("=", "="),
    "POS_GRAVE" to listOf("`", "`"),
    "POS_BACKSPACE" to listOf("⌫", "BACKSPACE"),
    // Row of QWER...
    "POS_TAB" to listOf("Tab", "TAB"),
    "POS_Q" to listOf("q", "q"),
    "POS_W" to listOf("w", "w"),
    "POS_E" to listOf("e", "e"),
    "POS_R" to listOf("r", "r"),
    "POS_T" to listOf("t", "t"),
    "POS_Y" to listOf("y", "y"),
    "POS_U" to listOf("u", "u"),
    "POS_I" to listOf("i", "i"),
    "POS_O" to listOf("o", "o"),
    "POS_P" to listOf("p", "p"),
    "POS_LEFTBRACE" to listOf("[", "["),
    "POS_RIGHTBRACE" to listOf("]", "]"),
    "POS_BACKSLASH" to listOf("\\", "\\"),
    // Row of ASDF...
    "POS_LEFTCTRL" to listOf("Ctrl", "LEFTCTRL"),
    "POS_A" to listOf("a", "a"),
    "POS_S" to listOf("s", "s"),
    "POS_D" to listOf("d", "d"),
    "POS_F" to listOf("f", "f"),
    "POS_G" to listOf("g", "g"),
    "POS_H" to listOf("h", "h"),
    "POS_J" to listOf("j", "j"),
    "POS_K" to listOf("k", "k"),
    "POS_L" to listOf("l", "l"),
    "POS_SEMICOLON" to listOf(";", ";"),
    "POS_APOSTROPHE" to listOf("'", "'"),
    "POS_ENTER" to listOf("↵", "ENTER"),
    // Row of ZXCV...
    "POS_LEFTSHIFT" to listOf("Shift", "LEFTSHIFT"),
    "POS_Z" to listOf("z", "z"),
    "POS_X" to listOf("x", "x"),
    "POS_C" to listOf("c", "c"),
    "POS_V" to listOf("v", "v"),
    "POS_B" to listOf("b", "b"),
    "POS_N" to listOf("n", "n"),
    "POS_M" to listOf("m", "m"),
    "POS_COMMA" to listOf(",", ","),
    "POS_DOT" to listOf(".", "."),
    "POS_SLASH" to listOf("/", "/"),
    // Others
    "POS_UP" to listOf("↑", "UP"),
    "POS_LEFT" to listOf("←", "LEFT"),
    "POS_DOWN" to listOf("↓", "DOWN"),
    "POS_RIGHT" to listOf("→", "RIGHT"),
    "POS_SPACE" to listOf("", "SPACE"),
    "POS_LANG1" to listOf("かな", "LANG1"),
    "POS_LANG2" to listOf("英数", "LANG2"),
)

val LAYOUT_SHIFTED_MAP: LayoutMap = mapOf(
    // Row of numbers
    "POS_ESC" to listOf("Esc", "ESC"),
    "POS_1" to listOf("!", "!"),
    "POS_2" to listOf("@", "@"),
    "POS_3" to listOf("#", "#"),
    "POS_4" to listOf("$", "$"),
    "POS_5" to listOf("%", "%"),
    "POS_6" to listOf("^", "^"),
    "POS_7" to listOf("&", "&"),
    "POS_8" to listOf("*", "*"),
    "POS_9" to listOf("(", "("),
    "POS_0" to listOf(")", ")"),
    "POS_MINUS" to listOf("_", "_"),
    "POS_EQUAL" to listOf("+", "+"),
    "POS_GRAVE" to listOf("~", "~"),
    "POS_BACKSPACE" to listOf("⌫", "BACKSPACE"),
    "POS_TAB" to listOf("Tab", "TAB"),
    // Row of QWER...
    "POS_Q" to listOf("Q", "Q"),
    "POS_W" to listOf("W", "W"),
    "POS_E" to listOf("E", "E"),
    "POS_R" to listOf("R", "R"),
    "POS_T" to listOf("T", "T"),
    "POS_Y" to listOf("Y", "Y"),
    "POS_U" to listOf("U", "U"),
    "POS_I" to listOf("I", "I"),
    "POS_O" to listOf("O", "O"),
    "POS_P" to listOf("P", "P"),
    "POS_LEFTBRACE" to listOf("{", "{"),
    "POS_RIGHTBRACE" to listOf("}", "}"),
    "POS_BACKSLASH" to listOf("|", "|"),
    // Row of ASDF...
    "POS_LEFTCTRL" to listOf("Ctrl", "LEFTCTRL"),
    "POS_A" to listOf("A", "A"),
    "POS_S" to listOf("S", "S"),
    "POS_D" to listOf("D", "D"),
    "POS_F" to listOf("F", "F"),
    "POS_G" to listOf("G", "G"),
    "POS_H" to listOf("H", "H"),
    "POS_J" to listOf("J", "J"),
    "POS_K" to listOf("K", "K"),
    "POS_L" to listOf("L", "L"),
    "POS_SEMICOLON" to listOf(":", ":"),
    "POS_APOSTROPHE" to listOf("\"", "\""),
    "POS_ENTER" to listOf("↵", "ENTER"),
    // Row of ZXCV...
    "POS_LEFTSHIFT" to listOf("Shift", "LEFTSHIFT"),
    "POS_Z" to listOf("Z", "Z"),
    "POS_X" to listOf("X", "X"),
    "POS_C" to listOf("C", "C"),
    "POS_V" to listOf("V", "V"),
    "POS_B" to listOf("B", "B"),
    "POS_N" to listOf("N", "N"),
    "POS_M" to listOf("M", "M"),
    "POS_COMMA" to listOf("<", "<"),
    "POS_DOT" to listOf(">", ">"),
    "POS_SLASH" to listOf("?", "?"),
    // Others
    "POS_UP" to listOf("↑", "UP"),
    "POS_LEFT" to listOf("←", "LEFT"),
    "POS_DOWN" to listOf("↓", "DOWN"),
    "POS_RIGHT" to listOf("→", "RIGHT"),
    "POS_SPACE" to listOf("", "SPACE"),
    "POS_LANG1" to listOf("かな", "LANG1"),
    "POS_LANG2" to listOf("英数", "LANG2"),
)

typealias LayoutData = List<List<Pair<String, Float>>>

val LAYOUT_DATA: LayoutData = listOf(
    listOf(
        Pair("POS_ESC", 1f),
        Pair("POS_1", 1f),
        Pair("POS_2", 1f),
        Pair("POS_3", 1f),
        Pair("POS_4", 1f),
        Pair("POS_5", 1f),
        Pair("POS_6", 1f),
        Pair("POS_7", 1f),
        Pair("POS_8", 1f),
        Pair("POS_9", 1f),
        Pair("POS_0", 1f),
        Pair("POS_MINUS", 1f),
        Pair("POS_EQUAL", 1f),
        Pair("POS_GRAVE", 1f),
        Pair("POS_BACKSPACE", 1f),
    ), listOf(
        Pair("POS_TAB", 1.5f),
        Pair("POS_Q", 1f),
        Pair("POS_W", 1f),
        Pair("POS_E", 1f),
        Pair("POS_R", 1f),
        Pair("POS_T", 1f),
        Pair("POS_Y", 1f),
        Pair("POS_U", 1f),
        Pair("POS_I", 1f),
        Pair("POS_O", 1f),
        Pair("POS_P", 1f),
        Pair("POS_LEFTBRACE", 1f),
        Pair("POS_RIGHTBRACE", 1f),
        Pair("POS_BACKSLASH", 1.5f),
    ), listOf(
        Pair("POS_LEFTCTRL", 1.75f),
        Pair("POS_A", 1f),
        Pair("POS_S", 1f),
        Pair("POS_D", 1f),
        Pair("POS_F", 1f),
        Pair("POS_G", 1f),
        Pair("POS_H", 1f),
        Pair("POS_J", 1f),
        Pair("POS_K", 1f),
        Pair("POS_L", 1f),
        Pair("POS_SEMICOLON", 1f),
        Pair("POS_APOSTROPHE", 1f),
        Pair("POS_ENTER", 2.25f),
    ), listOf(
        Pair("POS_LEFTSHIFT", 2.25f),
        Pair("POS_Z", 1f),
        Pair("POS_X", 1f),
        Pair("POS_C", 1f),
        Pair("POS_V", 1f),
        Pair("POS_B", 1f),
        Pair("POS_N", 1f),
        Pair("POS_M", 1f),
        Pair("POS_COMMA", 1f),
        Pair("POS_DOT", 1f),
        Pair("POS_SLASH", 1f),
        Pair("", 1f),
        Pair("POS_UP", 1f),
    ), listOf(
        Pair("", 2.75f),
        Pair("POS_", 1f),
        Pair("POS_", 1f),
        Pair("POS_LANG2", 1f),
        Pair("POS_SPACE", 2.5f),
        Pair("POS_LANG1", 1f),
        Pair("POS_", 1f),
        Pair("", 2f),
        Pair("POS_LEFT", 1f),
        Pair("POS_DOWN", 1f),
        Pair("POS_RIGHT", 1f),
    )
)

@Composable
fun Key(onClick: (String) -> Unit, label: String, value: String, modifier: Modifier = Modifier) {
    OutlinedButton(modifier = modifier,
        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
        shape = MaterialTheme.shapes.small,
        onClick = {
            Log.d("Key", "onClick($label)")
            onClick(value)
        }) {
        Text(
            text = label,
            overflow = TextOverflow.Clip,
            maxLines = 1,
        )
    }
}

fun getMaxWidth(layoutData: LayoutData): Float {
    var maxWidth = 0f
    for (row in layoutData) {
        var width = 0f
        for (items in row) {
            val (_: String, weight: Float) = items
            width += weight
        }
        maxWidth = max(width, maxWidth)
    }
    return maxWidth
}

@Composable
fun Layer(onClick: (String) -> Unit, layoutData: LayoutData, layoutMap: LayoutMap) {
    val maxWidth: Float = getMaxWidth(layoutData)
    Column {
        for (row in layoutData) {
            var width = 0f
            Row {
                for (items in row) {
                    val (pos: String, weight: Float) = items
                    width += weight
                    if (pos == "") {
                        Spacer(modifier = Modifier.weight(weight))
                        continue
                    }
                    val (label, value) = layoutMap[pos] ?: listOf("", "")
                    Key(
                        onClick = onClick,
                        label = label,
                        value = value,
                        modifier = Modifier.weight(weight)
                    )
                }
                if (width < maxWidth) {
                    Spacer(modifier = Modifier.weight(maxWidth - width))
                }
            }
        }
    }
}

@Composable
private fun MainView(onEvent: (Event) -> Unit) {
    val (message, setMessage) = remember { mutableStateOf("") }
    val onEventWithLogging: (Event) -> Unit = { event: Event ->
        onEvent(event)
        setMessage(logs.consume())
    }
    Column {
        TrackPad(onEvent = onEventWithLogging)
        Keyboard(onEvent = onEventWithLogging)
        Text(text = message)
    }
}

@Composable
private fun Keyboard(onEvent: (Event) -> Unit) {
    val (layout, setLayout) = remember { mutableStateOf("default") }
    val onClick: (String) -> Unit = { value: String ->
        var log = ""
        if (value == "LEFTSHIFT" || value == "RIGHTSHIFT") {
            log = "Shifted"
            setLayout(if (layout == "default") "shifted" else "default")
        } else {
            val event = Event().setKeyEvent(value)
            onEvent(event)
            setLayout("default")
        }
    }
    Column {
        val layoutMap: LayoutMap = if (layout == "shifted") LAYOUT_SHIFTED_MAP else LAYOUT_MAP
        Layer(onClick, LAYOUT_DATA, layoutMap)
    }
}

@Composable
private fun TrackPad(onEvent: (Event) -> Unit) {
    val onDrag: (PointerInputChange, Offset) -> Unit = { change, offset ->
        change.consume()
        val event = Event().setMouseEvent(dX = offset.x.toInt(), dY = offset.y.toInt())
        onEvent(event)
    }
    Row {
        Box(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .fillMaxWidth(0.5f)
                .fillMaxHeight(0.3f)
                .pointerInput(Unit) {
                    detectDragGestures(onDrag = onDrag)
                }
        )
        Button(
            onClick = {
                val event = Event().setMouseEvent(left=true)
                onEvent(event)
            }
        ) {
           Text("left")
        }
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Composable
fun LayersPreview() {
    HIDEmulatorTheme {
        Column {
            Layer({}, LAYOUT_DATA, LAYOUT_MAP)
            Layer({}, LAYOUT_DATA, LAYOUT_SHIFTED_MAP)
            TrackPad({})
        }
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Composable
fun KeyboardPreview() {
    HIDEmulatorTheme {
        Keyboard({})
    }
}
