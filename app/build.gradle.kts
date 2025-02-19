import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.laostack"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.laostack"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        // local.properties에서 base_url 키 값을 불러와 BASE_URL 필드에 저장
        buildConfigField("String", "BASE_URL", gradleLocalProperties(rootDir, providers).getProperty("base_url"))

    }

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
        compose = true
        // buildConfig 허용
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // 스플래시 스크린 의존성 추가
    implementation("androidx.core:core-splashscreen:1.0.1")

    // RESTful API 통신을 위한 의존성 추가
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    // JSON 응답을 Java 객체로 자동 변환하기 위한 의존성 추가
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // OkHttp 라이브러리 의존성 추가
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    // OkHttp 로깅 인터셉터 의존성 추가
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // navigation 의존성 추가
    implementation("androidx.navigation:navigation-compose:2.7.6")

    // 카메라 관련 의존성 추가
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("androidx.camera:camera-camera2:1.4.0-alpha02")
    implementation("androidx.camera:camera-lifecycle:1.4.0-alpha02")
    implementation ("androidx.camera:camera-view:1.4.0-alpha02")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}