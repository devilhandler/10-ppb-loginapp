package com.example.loginapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val dao = AppDatabase.get(this).userDao()
        setContent { MaterialTheme { LoginScreen(dao) } }
    }
}

@Composable
fun LoginScreen(dao: UserDao) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var success by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // Data awal: 1 user contoh (admin / admin123), diabaikan jika sudah ada
    LaunchedEffect(Unit) { dao.insert(User(username = "admin", password = "admin123")) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Text("Login", style = MaterialTheme.typography.headlineMedium)

        OutlinedTextField(
            value = username, onValueChange = { username = it },
            label = { Text("Username") }, singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") }, singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (username.isBlank() || password.isBlank()) {
                    success = false
                    message = "Username dan password tidak boleh kosong"
                } else scope.launch {
                    val user = dao.login(username.trim(), password)
                    success = user != null
                    message = if (user != null) "Login berhasil! Selamat datang, ${user.username}"
                    else "Login gagal: username atau password salah"
                }
            }
        ) { Text("Login") }

        if (message.isNotEmpty()) {
            Text(message, color = if (success) Color(0xFF2E7D32) else MaterialTheme.colorScheme.error)
        }
    }
}