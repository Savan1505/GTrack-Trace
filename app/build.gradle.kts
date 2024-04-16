plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
    id ("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.trace.gtrack"
    compileSdk = 34
    testOptions.unitTests.isReturnDefaultValues = true

    defaultConfig {
        applicationId = "com.trace.gtrack"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        manifestPlaceholders.putIfAbsent("appAuthRedirectScheme", "com.trace.gtrack")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }


    buildTypes {
        getByName("debug") {
            enableUnitTestCoverage = true
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
    flavorDimensions.add("app")
    productFlavors {
        create("dev") {
            buildConfigField(
                "String",
                "API_URL",
                "\"http://gtrackntraceapi.garimasystem.com/\""
//                "\"https://gtrackntraceapi.garimasystem.com/\""
//                "\"https://garimaapi.linde-le.com/\""

//            "Instance": "https://login.microsoftonline.com/",
//
//            "TenantId": "1562f007-09a4-4fcb-936b-e79246571fc7",
//
//            "ClientId": "7e51e960-b3dc-4902-aa26-512764761a35",
//
//            "RedirectUri": "https://www.garima.linde-le.com/",
            )
        }
        create("prod") {
            buildConfigField(
                "String",
                "API_URL",
                "\"https://gtrackntraceapi.garimasystem.com/\""
            )
        }
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    testImplementation("com.google.truth:truth:1.1.3")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    implementation("androidx.compose:compose-bom:2023.03.00")
    androidTestImplementation("androidx.compose:compose-bom:2023.03.00")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.6.5")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.vanniktech:android-image-cropper:4.5.0")
    implementation("com.jakewharton.timber:timber:4.7.1")
    implementation("io.github.muddz:styleabletoast:2.4.0")
    implementation("androidx.compose.ui:ui-tooling:1.6.5")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.6.5")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.ui:ui-tooling-preview:1.6.5")
    implementation("io.github.g00fy2.quickie:quickie-unbundled:1.9.0")
    implementation("com.github.bumptech.glide:okhttp3-integration:4.15.1")
    kapt("com.github.bumptech.glide:compiler:4.15.1")
    api("com.squareup.moshi:moshi-kotlin:1.11.0")
    api("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.jakewharton.timber:timber:4.7.1")

    debugImplementation("com.github.chuckerteam.chucker:library:3.5.2")
    releaseImplementation("com.github.chuckerteam.chucker:library-no-op:3.5.2")

    implementation ("com.google.firebase:firebase-core:21.1.1")
    implementation("com.google.firebase:firebase-auth-ktx:22.3.1")
    implementation("com.google.firebase:firebase-crashlytics-ktx:18.6.4")

    //Daggar Hilt
    implementation("com.google.dagger:hilt-android:2.45")
    kapt("com.google.dagger:hilt-compiler:2.45")
    implementation("androidx.activity:activity-ktx:1.8.2")

    //open id connect
    implementation("net.openid:appauth:0.7.1")
    implementation("com.squareup.okio:okio:1.17.5")//do not upgrade to version 2.x.x
}
