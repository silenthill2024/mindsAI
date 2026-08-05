package com.example.proyectodesdisint.ui.youtube

import android.annotation.SuppressLint
import android.graphics.Color
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import kotlinx.coroutines.delay

private const val PREVIEW_DURATION_MILLIS = 300_000L

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePreviewPlayer(
    videoId: String,
    startSeconds: Int,
    shouldPlay: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            setBackgroundColor(Color.BLACK)

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                mediaPlaybackRequiresUserGesture = false
                loadsImagesAutomatically = true
                allowContentAccess = true
                allowFileAccess = false
            }

            webChromeClient = WebChromeClient()

            webViewClient = object : WebViewClient() {
                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(
                        view,
                        request,
                        error
                    )
                }
            }
        }
    }

    LaunchedEffect(
        videoId,
        startSeconds
    ) {
        webView.loadDataWithBaseURL(
            "https://www.youtube.com",
            createYouTubeHtml(
                videoId = videoId,
                startSeconds = startSeconds
            ),
            "text/html",
            "UTF-8",
            null
        )
    }

    LaunchedEffect(
        shouldPlay,
        videoId,
        startSeconds
    ) {
        if (!shouldPlay) {
            webView.evaluateJavascript(
                "window.pausePreview && window.pausePreview();",
                null
            )

            return@LaunchedEffect
        }

        /*
         * El HTML conserva la solicitud de reproducción.
         * Cuando YouTube termina de cargar, onReady la ejecuta.
         */
        webView.evaluateJavascript(
            "window.requestPreview && window.requestPreview();",
            null
        )

        delay(PREVIEW_DURATION_MILLIS)

        webView.evaluateJavascript(
            "window.pausePreview && window.pausePreview();",
            null
        )
    }

    AndroidView(
        factory = {
            webView
        },
        update = {
            if (shouldPlay) {
                it.evaluateJavascript(
                    "window.requestPreview && window.requestPreview();",
                    null
                )
            } else {
                it.evaluateJavascript(
                    "window.pausePreview && window.pausePreview();",
                    null
                )
            }
        },
        modifier = modifier
    )

    DisposableEffect(webView) {
        onDispose {
            webView.evaluateJavascript(
                "window.pausePreview && window.pausePreview();",
                null
            )

            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.removeAllViews()
            webView.destroy()
        }
    }
}

private fun createYouTubeHtml(
    videoId: String,
    startSeconds: Int
): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta
                name="viewport"
                content="width=device-width, initial-scale=1.0,
                maximum-scale=1.0, user-scalable=no"
            >

            <meta
                name="referrer"
                content="strict-origin-when-cross-origin"
            >

            <style>
                html,
                body {
                    margin: 0;
                    padding: 0;
                    width: 100%;
                    height: 100%;
                    overflow: hidden;
                    background: #000000;
                }

                #player {
                    width: 100%;
                    height: 100%;
                }
            </style>
        </head>

        <body>
            <div id="player"></div>

            <script src="https://www.youtube.com/iframe_api"></script>

            <script>
                var player = null;
                var playerReady = false;
                var previewRequested = false;

                function onYouTubeIframeAPIReady() {
                    player = new YT.Player("player", {
                        videoId: "$videoId",

                        playerVars: {
                            autoplay: 0,
                            controls: 1,
                            playsinline: 1,
                            rel: 0,
                            mute: 1,
                            start: $startSeconds,
                            enablejsapi: 1,
                            origin: "https://www.youtube.com"
                        },

                        events: {
                            onReady: function(event) {
                                playerReady = true;
                                event.target.mute();
                                event.target.seekTo(
                                    $startSeconds,
                                    true
                                );

                                if (previewRequested) {
                                    event.target.playVideo();
                                }
                            },

                            onError: function(event) {
                                console.log(
                                    "YouTube error: " +
                                    event.data
                                );
                            },

                            onAutoplayBlocked: function() {
                                console.log(
                                    "Autoplay bloqueado"
                                );
                            }
                        }
                    });
                }

                window.requestPreview = function() {
                    previewRequested = true;

                    if (
                        playerReady &&
                        player &&
                        player.playVideo
                    ) {
                        player.mute();
                        player.seekTo(
                            $startSeconds,
                            true
                        );
                        player.playVideo();
                    }
                };

                window.pausePreview = function() {
                    previewRequested = false;

                    if (
                        playerReady &&
                        player &&
                        player.pauseVideo
                    ) {
                        player.pauseVideo();
                    }
                };
            </script>
        </body>
        </html>
    """.trimIndent()
}
