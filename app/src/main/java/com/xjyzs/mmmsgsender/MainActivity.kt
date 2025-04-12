package com.xjyzs.mmmsgsender

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.gson.Gson
import com.xjyzs.mmmsgsender.ui.theme.MmMsgSenderTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import kotlin.concurrent.thread

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MmMsgSenderTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Ui()
                }
            }
        }
    }
}

data class Token(
    val access_token:String,
    val expires_in:Int
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar() {
    var showMenu by remember { mutableStateOf(false) }
    val context= LocalContext.current
    CenterAlignedTopAppBar(
        title = { Text("MmMsgSender") },
        actions = {
            // 菜单按钮
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "更多选项"
                )
            }

            // 下拉菜单
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("设置") },
                    onClick = {
                        showMenu = false
                        val intent=Intent(context,SettingsActivity::class.java)
                        context.startActivity(intent)
                    }
                )
            }
        }
    )
}

@SuppressLint("CommitPrefEdits")
@Composable
fun Ui() {
    var txt by remember { mutableStateOf("") }
    var txt1 by remember { mutableStateOf("") }
    var toastData by remember { mutableStateOf("") }
    val context= LocalContext.current
    val s=context.getSharedPreferences("access_token", Context.MODE_PRIVATE)
    var p=context.getSharedPreferences("settings",Context.MODE_PRIVATE)
    if (toastData!=""){
        Toast.makeText(context,toastData,Toast.LENGTH_SHORT).show()
        toastData=""
    }
    Scaffold(
        topBar = { MyTopAppBar() }
    ) { innerPadding ->
        Column(Modifier.fillMaxSize().padding(innerPadding).wrapContentSize(Alignment.Center)) {
            TextField(label = { Text("内容") }, value = txt, onValueChange = { txt = it })
            TextField(label = { Text("内容") }, value = txt1, onValueChange = { txt1 = it })
            Button({
                val appID = p.getString("appID","")
                val appsecret = p.getString("appsecret","")
                val touser = p.getString("touser","")
                val template_id = p.getString("template_id","")
                val url = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=${appID}&secret=${appsecret}"
                thread {
                    val client=OkHttpClient()
                    var access_token=""
                    if (s.getInt("time",0)<System.currentTimeMillis()/1000){
                        val token = Gson().fromJson(client.newCall(Request.Builder().url(url).build()).execute().body?.string(), Token::class.java)
                        access_token=token.access_token
                        with (s.edit()){
                            putString("access_token",access_token)
                            putInt("time", (System.currentTimeMillis()/1000+token.expires_in/2).toInt())
                            apply()
                        }
                    }else{
                        access_token=s.getString("access_token","")!!
                    }

                    val json = """
                    {
                      "touser": "$touser",
                      "template_id": "$template_id",
                      "url": "https://github.com/xjyzs",
                      "data": {
                        "text" : {
                          "value": "$txt"
                        },
                        "text1" : {
                          "value": "$txt1"
                        }
                      }
                    }
                    """.trimIndent()
                    val requestBody = json.toRequestBody()
                    val request = Request.Builder()
                        .url("https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=$access_token")
                        .post(requestBody)
                        .build()
                    toastData=client.newCall(request).execute().body?.string()!!
                }
            }) {
                Text("发送")
            }
        }
    }
}

fun get(url: String, onResponse: (String) -> Unit) {
    val client = OkHttpClient()
    val request = Request.Builder()
        .url(url)
        .build()
    thread {
        val response: Response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val responseData = response.body?.string()
            onResponse(responseData ?: "No response")
        }
    }
}