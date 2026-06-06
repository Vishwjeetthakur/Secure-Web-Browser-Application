package com.vishwajeet.securewebbrowserapplication.ui.screen

import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.vishwajeet.securewebbrowserapplication.R
import com.vishwajeet.securewebbrowserapplication.data.model.AuthResultState
import com.vishwajeet.securewebbrowserapplication.ui.viewmodel.AuthViewModel

@Composable
fun SignInScreen(
    viewModel: AuthViewModel,
    onNavigateToHome: () -> Unit
) {
    val context = LocalContext.current
    val WEB_CLIENT_ID = "81380480199-2ku6emtk596ululfsa2c9vou2lsr1m5c.apps.googleusercontent.com"


    LaunchedEffect(viewModel.authState) {
        if (viewModel.authState is AuthResultState.Success) {
            onNavigateToHome()
        }
    }

    val gso = remember {
        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .build()
    }

    val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

    val authLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(activityResult.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    // Just pass token to ViewModel now, execution is independent
                    viewModel.handleFirebaseGoogleSignIn(idToken) {}
                } else {
                    Toast.makeText(context, "Google Token Extraction Failure", Toast.LENGTH_SHORT).show()
                }
            } catch (e: ApiException) {
                Log.e("GOOGLE_AUTH", "Sign in configuration invalid rules. Code: ${e.statusCode}", e)
                Toast.makeText(context, "Auth Error Code: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F111A)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier.padding(top = 64.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.web_link),
                        contentDescription = "App Security Logo Link",
                        modifier = Modifier.size(100.dp)
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "WebToNative",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Turn your web app into a native experience",
                    fontSize = 15.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "SIGN IN WITH",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Error feedback block UI addition
                if (viewModel.authState is AuthResultState.Error) {
                    Text(
                        text = (viewModel.authState as AuthResultState.Error).message,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(bottom = 8.dp),
                        textAlign = TextAlign.Center
                    )
                }

                if (viewModel.authState is AuthResultState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(32.dp),
                        color = Color.White
                    )
                } else {
                    Button(
                        onClick = {
                            val signInIntent = googleSignInClient.signInIntent
                            authLauncher.launch(signInIntent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(27.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {

                        Image(
                            painter = painterResource(R.drawable.google),
                            contentDescription = "google",
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Continue with Google",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
                Text(
                    text = "By continuing, you agree to our Terms of Service\nand Privacy Policy",
                    fontSize = 11.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
            }
        }
    }
}