
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1"
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.kakaologin"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.kakaologin"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true // BuildConfig 생성 활성화
        viewBinding = true
    }
}

dependencies {
    implementation(libs.v2.all) // 전체 모듈 설치, 2.11.0 버전부터 지원
    implementation(libs.v2.user) // 카카오 로그인 API 모듈
    implementation(libs.v2.share) // 카카오톡 공유 API 모듈
    implementation(libs.v2.talk) // 카카오톡 채널, 카카오톡 소셜, 카카오톡 메시지 API 모듈
    implementation(libs.v2.friend) // 피커 API 모듈
    implementation(libs.v2.navi) // 카카오내비 API 모듈
    implementation(libs.v2.cert) // 카카오톡 인증 서비스 API 모듈

    implementation (libs.lottie)

    implementation(libs.firebase.database)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)
    implementation(platform(libs.firebase.bom))
    implementation(libs.com.google.firebase.firebase.database)
    implementation(libs.google.firebase.database)

    implementation (libs.play.services.location)
    implementation(libs.play.services.maps)

    implementation (libs.glide)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}