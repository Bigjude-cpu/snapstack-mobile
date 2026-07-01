package com.lensbooks.app

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.FirebaseApp
import com.lensbooks.app.auth.AuthScreen
import com.lensbooks.app.auth.AuthViewModel
import com.lensbooks.app.data.repository.DatabaseViewModel
import com.lensbooks.app.ui.screens.CrmScreen
import com.lensbooks.app.ui.screens.FeedScreen
import com.lensbooks.app.ui.screens.SchedulerScreen
import com.lensbooks.app.ui.theme.LensbooksTheme

class MainActivity : ComponentActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    private val databaseViewModel: DatabaseViewModel by viewModels()

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase
        initializeFirebase()

        setContent {
            LensbooksTheme {
                val currentUser by authViewModel.currentUser.collectAsState()
                val loading by authViewModel.loading.collectAsState()

                when {
                    loading -> {
                        // Loading screen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    currentUser == null -> {
                        // Authentication screen
                        AuthScreen(
                            viewModel = authViewModel,
                            onAuthSuccess = { /* State updates automatically */ }
                        )
                    }
                    else -> {
                        // Main dashboard
                        DashboardLayout(
                            authViewModel = authViewModel,
                            databaseViewModel = databaseViewModel
                        )
                    }
                }
            }
        }
    }

    private fun initializeFirebase() {
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.i(TAG, "Firebase initialized successfully")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization failed", e)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardLayout(
    authViewModel: AuthViewModel,
    databaseViewModel: DatabaseViewModel
) {
    var activeTab by remember { mutableStateOf("feed") }
    val currentUser by authViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column(modifier = Modifier.padding(start = 4.dp)) {
                        Text(
                            text = "LENSBOOKS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 4.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Aperture & Artistry Combined",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary,
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                },
                actions = {
                    Text(
                        text = currentUser?.email?.take(20) ?: "User",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    IconButton(onClick = { authViewModel.signOut() }) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "Sign out",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = activeTab == "feed",
                    onClick = { activeTab = "feed" },
                    icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = "Feed") },
                    label = { Text("Feed", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )

                NavigationBarItem(
                    selected = activeTab == "crm",
                    onClick = { activeTab = "crm" },
                    icon = { Icon(Icons.Default.SupervisorAccount, contentDescription = "CRM") },
                    label = { Text("CRM", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )

                NavigationBarItem(
                    selected = activeTab == "scheduler",
                    onClick = { activeTab = "scheduler" },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = "Scheduler") },
                    label = { Text("Scheduler", fontWeight = FontWeight.Bold, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (activeTab) {
                "feed" -> FeedScreen(viewModel = databaseViewModel)
                "crm" -> CrmScreen(viewModel = databaseViewModel)
                "scheduler" -> SchedulerScreen(viewModel = databaseViewModel)
                else -> FeedScreen(viewModel = databaseViewModel)
            }
        }
    }
}