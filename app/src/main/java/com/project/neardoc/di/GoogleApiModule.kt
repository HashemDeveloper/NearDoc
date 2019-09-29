package com.project.neardoc.di

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.project.neardoc.R
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object GoogleApiModule {
    @Singleton
    @Provides
    @JvmStatic
    internal fun provideGoogleSignInOption(context: Context): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.resources.getString(R.string.google_sign_in_api_key))
            .requestEmail()
            .build()
    }
    @Singleton
    @Provides
    @JvmStatic
    internal fun provideGoogleSignInClient(googleSignInOptions: GoogleSignInOptions, context: Context): GoogleSignInClient {
        return GoogleSignIn.getClient(context, googleSignInOptions)
    }
}