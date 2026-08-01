package com.example.proyectodesdisint

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.proyectodesdisint.navigation.AppNavigation
import com.example.mindsai.streaming.StreamingClient
import com.example.mindsai.streaming.WearSyncManager
import com.example.proyectodesdisint.ui.theme.ProyectoDesDisIntTheme

class MainActivity : ComponentActivity() {

    private lateinit var streamingClient: StreamingClient
    private lateinit var wearSyncManager: WearSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        wearSyncManager = WearSyncManager(this)

        streamingClient = StreamingClient { json ->

            wearSyncManager.sendEventToWatch(
                json = json,
                path = "/stream_event"
            )
        }

        streamingClient.connect()

        setContent {
            ProyectoDesDisIntTheme {
                AppNavigation()
            }
        }
    }

    override fun onDestroy() {
        streamingClient.disconnect()
        super.onDestroy()
    }
}

@Composable
fun Greeting(
    name: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ProyectoDesDisIntTheme {
        Greeting("Android")
    }
}
