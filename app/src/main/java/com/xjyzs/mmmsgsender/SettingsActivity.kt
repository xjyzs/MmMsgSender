package com.xjyzs.mmmsgsender

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.xjyzs.mmmsgsender.ui.theme.MmMsgSenderTheme

class SettingsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MmMsgSenderTheme {
                Surface {
                    SettingsUi()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsUi() {
    val context = LocalContext.current
    val p = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                navigationIcon = {
                    IconButton(onClick = { (context as ComponentActivity).finish() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "")
                    }
                }
            )
        }) { innerPadding ->
        Column(Modifier.fillMaxSize().wrapContentSize(Alignment.Center).padding(innerPadding)) {
            var appID by remember { mutableStateOf(p.getString("appID", "")) }
            var appsecret by remember { mutableStateOf(p.getString("appsecret", "")) }
            var touser by remember { mutableStateOf(p.getString("touser", "")) }
            var template_id by remember { mutableStateOf(p.getString("template_id", "")) }
            TextField(label = { Text("appID") }, value = appID!!, onValueChange = { appID = it })
            TextField(
                label = { Text("appsecret") },
                value = appsecret!!,
                onValueChange = { appsecret = it })
            TextField(label = { Text("touser") }, value = touser!!, onValueChange = { touser = it })
            TextField(
                label = { Text("template_id") },
                value = template_id!!,
                onValueChange = { template_id = it })
            Button({
                with(p.edit()) {
                    putString("appID", appID)
                    putString("appsecret", appsecret)
                    putString("touser", touser)
                    putString("template_id", template_id)
                    apply()
                }
            }) {
                Text("保存")
            }
        }
    }
}