package com.dream.virtualvacation.ui.screens

import android.app.Activity
import android.content.pm.ActivityInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.dream.virtualvacation.data.model.City
import com.dream.virtualvacation.ui.theme.VirtualVacationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    city: City,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    var isFullscreen by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    
    // Add timeout for loading
    LaunchedEffect(isLoading) {
        if (isLoading) {
            kotlinx.coroutines.delay(10000) // 10 second timeout
            if (isLoading) {
                hasError = true
                isLoading = false
            }
        }
    }
    
    // Set landscape orientation when fullscreen
    LaunchedEffect(isFullscreen) {
        val activity = context as? Activity
        activity?.requestedOrientation = if (isFullscreen) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR
        }
    }
    
    if (isFullscreen) {
        FullscreenVideoPlayer(
            city = city,
            onExitFullscreen = { isFullscreen = false },
            onBackClick = onBackClick
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = city.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            modifier = modifier
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else if (hasError) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Unable to load video",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { 
                                    hasError = false
                                    isLoading = true
                                    // Reload the WebView by triggering recomposition
                                }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
                
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.apply {
                                javaScriptEnabled = true
                                domStorageEnabled = true
                                mediaPlaybackRequiresUserGesture = false
                                allowFileAccess = true
                                allowContentAccess = true
//                                setAppCacheEnabled(true)
                                cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                                mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                                setSupportZoom(false)
                                builtInZoomControls = false
                                displayZoomControls = false
                            }
                            
                            setBackgroundColor(android.graphics.Color.BLACK)
                            
                            webChromeClient = object : WebChromeClient() {
                                override fun onShowCustomView(view: android.view.View?, callback: CustomViewCallback?) {
                                    super.onShowCustomView(view, callback)
                                    isFullscreen = true
                                }
                                
                                override fun onHideCustomView() {
                                    super.onHideCustomView()
                                    isFullscreen = false
                                }
                                
                                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                                    super.onProgressChanged(view, newProgress)
                                    if (newProgress == 100) {
                                        isLoading = false
                                    }
                                }
                            }
                            
                            webViewClient = object : WebViewClient() {
                                override fun onPageFinished(view: WebView?, url: String?) {
                                    super.onPageFinished(view, url)
                                    isLoading = false
                                    // Inject JavaScript to ensure video is visible
                                    view?.evaluateJavascript(
                                        """
                                        document.body.style.backgroundColor = 'black';
                                        var video = document.querySelector('video');
                                        if (video) {
                                            video.style.width = '100%';
                                            video.style.height = '100%';
                                            video.style.objectFit = 'contain';
                                        }
                                        """.trimIndent(), null
                                    )
                                }
                                
                                override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                                    super.onReceivedError(view, errorCode, description, failingUrl)
                                    isLoading = false
                                    hasError = true
                                }
                            }
                            
                            // Load YouTube embed URL with better parameters
                            val embedUrl = "https://www.youtube.com/embed/${city.videoId}?autoplay=1&rel=0&modestbranding=1&fs=1&controls=1&showinfo=0&iv_load_policy=3&cc_load_policy=0"
                            loadUrl(embedUrl)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .background(Color.Black)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Enjoy your virtual vacation to ${city.name}!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Button(
                    onClick = { isFullscreen = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Watch in Fullscreen")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FullscreenVideoPlayer(
    city: City,
    onExitFullscreen: () -> Unit,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    var isLoading by remember { mutableStateOf(true) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Fullscreen WebView
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        allowFileAccess = true
                        allowContentAccess = true
//                        setAppCacheEnabled(true)
                        cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                        mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        setSupportZoom(false)
                        builtInZoomControls = false
                        displayZoomControls = false
                    }
                    
                    setBackgroundColor(android.graphics.Color.BLACK)
                    
                    webChromeClient = object : WebChromeClient() {
                        override fun onHideCustomView() {
                            super.onHideCustomView()
                            onExitFullscreen()
                        }
                        
                        override fun onProgressChanged(view: WebView?, newProgress: Int) {
                            super.onProgressChanged(view, newProgress)
                            if (newProgress == 100) {
                                isLoading = false
                            }
                        }
                    }
                    
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            isLoading = false
                            // Inject JavaScript to ensure video is visible in fullscreen
                            view?.evaluateJavascript(
                                """
                                document.body.style.backgroundColor = 'black';
                                document.body.style.margin = '0';
                                document.body.style.padding = '0';
                                var video = document.querySelector('video');
                                if (video) {
                                    video.style.width = '100vw';
                                    video.style.height = '100vh';
                                    video.style.objectFit = 'contain';
                                    video.style.position = 'fixed';
                                    video.style.top = '0';
                                    video.style.left = '0';
                                }
                                """.trimIndent(), null
                            )
                        }
                        
                        override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                            super.onReceivedError(view, errorCode, description, failingUrl)
                            isLoading = false
                        }
                    }
                    
                    // Load YouTube embed URL for fullscreen with better parameters
                    val embedUrl = "https://www.youtube.com/embed/${city.videoId}?autoplay=1&rel=0&modestbranding=1&fs=1&controls=1&showinfo=0&iv_load_policy=3&cc_load_policy=0"
                    loadUrl(embedUrl)
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        
        // Loading indicator
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Exit fullscreen button
        IconButton(
            onClick = onExitFullscreen,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Exit Fullscreen",
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun VideoPlayerScreenPreview() {
    VirtualVacationTheme {
        VideoPlayerScreen(
            city = City("Naples 🇮🇹", "IHXZnU2bmc8"),
            onBackClick = { /* Preview action */ }
        )
    }
}

@Preview(showBackground = true, name = "Video Player - Long Name")
@Composable
fun VideoPlayerScreenLongNamePreview() {
    VirtualVacationTheme {
        VideoPlayerScreen(
            city = City("Buenos Aires 🇦🇷", "-TPJot7-HTs"),
            onBackClick = { /* Preview action */ }
        )
    }
}
