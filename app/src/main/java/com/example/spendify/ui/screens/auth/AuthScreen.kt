package com.example.spendify.ui.screens.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.spendify.R
import com.example.spendify.ui.components.AmbientGlowBackground
import com.example.spendify.ui.components.GlassCard
import com.example.spendify.ui.theme.ErrorDark
import com.example.spendify.ui.theme.GlassBorderDark
import com.example.spendify.ui.theme.OnPrimaryDark
import com.example.spendify.ui.theme.OnSurfaceDark
import com.example.spendify.ui.theme.OnSurfaceVariantDark
import com.example.spendify.ui.theme.PillShape
import com.example.spendify.ui.theme.PrimaryDark
import com.example.spendify.ui.theme.SurfaceContainerHighDark

import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onAuthSuccess()
            viewModel.resetAuthState()
        }
    }

    AmbientGlowBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Optional Top Back Button Row
            if (onNavigateBack != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(PillShape)
                            .background(SurfaceContainerHighDark)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back",
                            tint = OnSurfaceDark
                        )
                    }
                }
            }

            // App Branding Logo & Title
            Image(
                painter = painterResource(id = R.drawable.spendify_logo),
                contentDescription = "Spendify Logo",
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(18.dp))
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Spendify",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                color = PrimaryDark
            )

            Text(
                text = if (uiState.isSignUp) "Create your personal account" else "Welcome back to smart finances",
                style = MaterialTheme.typography.bodyLarge,
                color = OnSurfaceVariantDark,
                modifier = Modifier.padding(top = 8.dp, bottom = 28.dp)
            )

            // Glass Container for Form
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                isHeavy = true
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Segmented Mode Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(PillShape)
                            .background(SurfaceContainerHighDark)
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(PillShape)
                                .background(if (!uiState.isSignUp) PrimaryDark else Color.Transparent)
                                .clickable { if (uiState.isSignUp) viewModel.toggleMode() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign In",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (!uiState.isSignUp) OnPrimaryDark else OnSurfaceVariantDark
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxSize()
                                .clip(PillShape)
                                .background(if (uiState.isSignUp) PrimaryDark else Color.Transparent)
                                .clickable { if (!uiState.isSignUp) viewModel.toggleMode() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sign Up",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = if (uiState.isSignUp) OnPrimaryDark else OnSurfaceVariantDark
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Full Name Field (Sign Up only)
                    if (uiState.isSignUp) {
                        Column {
                            OutlinedTextField(
                                value = uiState.fullName,
                                onValueChange = viewModel::onFullNameChanged,
                                label = { Text("Full Name") },
                                isError = uiState.fullNameError != null,
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryDark)
                                },
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Text,
                                    imeAction = ImeAction.Next
                                ),
                                keyboardActions = KeyboardActions(
                                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryDark,
                                    unfocusedBorderColor = GlassBorderDark,
                                    focusedTextColor = OnSurfaceDark,
                                    unfocusedTextColor = OnSurfaceDark,
                                    focusedLabelColor = PrimaryDark,
                                    unfocusedLabelColor = OnSurfaceVariantDark,
                                    errorBorderColor = ErrorDark
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (uiState.fullNameError != null) {
                                Text(
                                    text = uiState.fullNameError!!,
                                    color = ErrorDark,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }
                    }

                    // Email Field
                    Column {
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = viewModel::onEmailChanged,
                            label = { Text("Email Address") },
                            isError = uiState.emailError != null,
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = PrimaryDark)
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) }
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryDark,
                                unfocusedBorderColor = GlassBorderDark,
                                focusedTextColor = OnSurfaceDark,
                                unfocusedTextColor = OnSurfaceDark,
                                focusedLabelColor = PrimaryDark,
                                unfocusedLabelColor = OnSurfaceVariantDark,
                                errorBorderColor = ErrorDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (uiState.emailError != null) {
                            Text(
                                text = uiState.emailError!!,
                                color = ErrorDark,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }

                    // Password Field
                    Column {
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = viewModel::onPasswordChanged,
                            label = { Text("Password") },
                            isError = uiState.passwordError != null,
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryDark)
                            },
                            trailingIcon = {
                                IconButton(onClick = viewModel::togglePasswordVisibility) {
                                    Icon(
                                        imageVector = if (uiState.isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Toggle password visibility",
                                        tint = OnSurfaceVariantDark
                                    )
                                }
                            },
                            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = if (uiState.isSignUp) ImeAction.Next else ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                                onDone = {
                                    focusManager.clearFocus()
                                    viewModel.submitAuth()
                                }
                            ),
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryDark,
                                unfocusedBorderColor = GlassBorderDark,
                                focusedTextColor = OnSurfaceDark,
                                unfocusedTextColor = OnSurfaceDark,
                                focusedLabelColor = PrimaryDark,
                                unfocusedLabelColor = OnSurfaceVariantDark,
                                errorBorderColor = ErrorDark
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (uiState.passwordError != null) {
                            Text(
                                text = uiState.passwordError!!,
                                color = ErrorDark,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                            )
                        }
                    }

                    // Confirm Password Field (Sign Up only)
                    if (uiState.isSignUp) {
                        Column {
                            OutlinedTextField(
                                value = uiState.confirmPassword,
                                onValueChange = viewModel::onConfirmPasswordChanged,
                                label = { Text("Confirm Password") },
                                isError = uiState.confirmPasswordError != null,
                                leadingIcon = {
                                    Icon(Icons.Default.Lock, contentDescription = null, tint = PrimaryDark)
                                },
                                trailingIcon = {
                                    IconButton(onClick = viewModel::toggleConfirmPasswordVisibility) {
                                        Icon(
                                            imageVector = if (uiState.isConfirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                            contentDescription = "Toggle confirm password visibility",
                                            tint = OnSurfaceVariantDark
                                        )
                                    }
                                },
                                visualTransformation = if (uiState.isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Password,
                                    imeAction = ImeAction.Done
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()
                                        viewModel.submitAuth()
                                    }
                                ),
                                singleLine = true,
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryDark,
                                    unfocusedBorderColor = GlassBorderDark,
                                    focusedTextColor = OnSurfaceDark,
                                    unfocusedTextColor = OnSurfaceDark,
                                    focusedLabelColor = PrimaryDark,
                                    unfocusedLabelColor = OnSurfaceVariantDark,
                                    errorBorderColor = ErrorDark
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                            if (uiState.confirmPasswordError != null) {
                                Text(
                                    text = uiState.confirmPasswordError!!,
                                    color = ErrorDark,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(start = 8.dp, top = 4.dp)
                                )
                            }
                        }
                    }

                    // General Error Message
                    if (uiState.errorMessage != null) {
                        Text(
                            text = uiState.errorMessage!!,
                            color = ErrorDark,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }

                    // Main Submit Button
                    Button(
                        onClick = viewModel::submitAuth,
                        enabled = !uiState.isLoading,
                        shape = PillShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryDark,
                            contentColor = OnPrimaryDark
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = OnPrimaryDark, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (uiState.isSignUp) "Create Account" else "Sign In",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
