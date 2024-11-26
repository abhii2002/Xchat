package com.blissvine.xchat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/*
Here we are registering hilt at application level.
by using @HiltAndroidApp annotation.
 */

@HiltAndroidApp
class XchatApplication: Application()