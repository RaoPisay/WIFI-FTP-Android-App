package noadd

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import noadd.ui.theme.FTPAppTheme
import java.net.Inet4Address
import java.net.NetworkInterface
import java.util.Collections

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
            ); }
        setContent {
            FTPAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FTPServerScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun FTPServerScreen(modifier: Modifier = Modifier) {
    var ipAddress by remember { mutableStateOf("Fetching...") }
    val port = 2221

    LaunchedEffect(Unit) {
        ipAddress = getLocalIpAddress()
    }

    LaunchedEffect(Unit) {
        FTPServer.start()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BorderBox(label = "IP", value = ipAddress)
        Spacer(modifier = Modifier.height(16.dp))
        BorderBox(label = "Port", value = port.toString())
        Spacer(modifier = Modifier.height(16.dp))
        BorderBox(
            label = "Note",
            value = "Use any FTP Client to transfer files from this phone to your device.\nAnd make sure both devices are in same wifi network"
        )
    }
}

@Composable
private fun BorderBox(label: String, value: String) {
    Row(
        modifier = Modifier
            //.border(6.dp, Color.Red, shape = RoundedCornerShape(12.dp))
            .border(4.dp, Color.Black, shape = RoundedCornerShape(8.dp)) // Inner black border
            .padding(16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label: ", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.Blue
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = value, fontSize = 18.sp, fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewFTPServerScreen() {
    FTPServerScreen()
}

// Function to get the local IP address
fun getLocalIpAddress(): String {
    //Inet4Address.
    try {
        val networks = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (network in networks) {
            val inetAddresses = network.inetAddresses
            for (inetAddress in inetAddresses) {
                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                    return inetAddress.hostAddress ?: "Unknown IP"
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return "Unknown IP"
}