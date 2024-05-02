package org.taiyaki.hidemu

import android.content.Context
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
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
const val KEY_LEFTCTRL  = 0xE0  // Keyboard LeftControl
const val KEY_LEFTSHIFT = 0xE1  // Keyboard LeftShift
const val KEY_RIGHTCTRL = 0xE4  // Keyboard RightControl
const val KEY_RIGHTSHIFT = 0xE5 // Keyboard LeftShift

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
    "LEFTCTRL" to Pair(0x00, KEY_LEFTCTRL),
    "LEFTSHIFT" to Pair(0x00, KEY_LEFTSHIFT),
    "RIGHTCTRL" to Pair(0x00, KEY_RIGHTCTRL),
    "RIGHTSHIFT" to Pair(0x00, KEY_RIGHTSHIFT),
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
        val code: Int = packet[5].toInt()
        return CODEMAP[code] ?: "unknown"
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
            0x57,
            0xAB.toByte(),
            0x00,
            0x02,
            0x08,
            modifiers.toByte(),
            0x00,
            hidCode.toByte(),
            0x00,
            0x00,
            0x00,
            0x00,
            0x00,
            0x00
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
    "POS_COMMA" to listOf(",", ","),
    "POS_DOT" to listOf(".", "."),
    "POS_SLASH" to listOf("?", "?"),
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
    ), listOf(
        Pair("", 1.75f),
        Pair("POS_", 1f),
        Pair("POS_", 1f),
        Pair("POS_", 1f),
        Pair("POS_SPACE", 2.5f),
        Pair("POS_", 1f),
        Pair("POS_", 1f),
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
            label, overflow = TextOverflow.Ellipsis
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
fun Keyboard(onKeyInput: (String) -> String) {
    val (layout, setLayout) = remember { mutableStateOf("default") }
    val (message, setMessage) = remember { mutableStateOf("") }
    val onClick: (String) -> Unit = { value: String ->
        var log = ""
        if (value == "LEFTSHIFT" || value == "RIGHTSHIFT") {
            log = "Shifted"
            setLayout("shifted")
        } else {
            log = onKeyInput(value)
            setLayout("default")
        }
        setMessage(log)
    }
    Column {
        val layoutMap: LayoutMap = if (layout == "shifted") LAYOUT_SHIFTED_MAP else LAYOUT_MAP
        Layer(onClick, LAYOUT_DATA, layoutMap)
        // Layer(onClick, LAYOUT_DATA, LAYOUT2_MAP)
        Text(
            text = message
        )
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Composable
fun KeyboardPreview() {
    HIDEmulatorTheme {
        Keyboard(onKeyInput = { "" })
    }
}

@Preview(showBackground = true, device = "spec:parent=pixel_5")
@Composable
fun LayersPreview() {
    HIDEmulatorTheme {
        Column {
            Layer({}, LAYOUT_DATA, LAYOUT_MAP)
            Layer({}, LAYOUT_DATA, LAYOUT_SHIFTED_MAP)
        }
    }
}