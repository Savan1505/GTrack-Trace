// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id ("com.android.library") version "7.2.2" apply false
    id ("com.google.dagger.hilt.android") version "2.44" apply false
    id("com.google.gms.google-services") version "4.4.1" apply false
    id("com.google.firebase.crashlytics") version "2.9.9" apply false
}
allprojects {
    configurations.all {
        resolutionStrategy.force ("org.objenesis:objenesis:2.6")
    }
}