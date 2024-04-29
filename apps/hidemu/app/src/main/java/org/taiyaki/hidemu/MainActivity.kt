package org.taiyaki.hidemu

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
import org.taiyaki.hidemu.ui.theme.HIDEmulatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HIDEmulatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background) {
                    Keyboard()
                }
            }
        }
    }
}

@Composable
fun Keyboard() {
    val context = LocalContext.current
    Column {
        Button(onClick = {
            Toast.makeText(context, "clicked", Toast.LENGTH_SHORT).show()
            Log.d("Button", "onClick")
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
fun GreetingPreview() {
    HIDEmulatorTheme {
        Keyboard()
    }
}