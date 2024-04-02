plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    //Add the kapt plugin for annotation processing
    id("kotlin-kapt")
}

android {
    namespace = "com.example.rapidcar_v01"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.rapidcar_v01"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    /*defaultConfig {
        applicationId = "com.example.rapidcar_v01"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }*/

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {
    //Fragment para la animaciones
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.6.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.6.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
    //admob
    implementation ("com.google.android.gms:play-services-ads:20.5.0")



    //Fragment, DataBinding, and ViewBinding libraries
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("androidx.databinding:databinding-runtime:7.1.0")
    implementation("androidx.databinding:viewbinding:3.1.0")

    //Retrofit and other libraries
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.recyclerview:recyclerview:1.2.0")

    //OkHttp  & Interceptor
    implementation("com.squareup.okhttp3:okhttp:4.9.3")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.13.0")

    //Use kapt for annotation processing with Glide
    kapt("com.github.bumptech.glide:compiler:4.13.0")

    //json converter
    implementation("com.squareup.moshi:moshi-kotlin:1.12.0")

    //Other dependencies
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    //Testing libraries
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}
