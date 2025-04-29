plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.ungdungdatdoanonlinenhvietkitchensg"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.ungdungdatdoanonlinenhvietkitchensg"
        minSdk = 22
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }

    packaging {
        resources {
            excludes += listOf(
                "META-INF/NOTICE.md",  // Đã thêm từ lần trước
                "META-INF/LICENSE.md"  // Thêm mới để xử lý lỗi này
            )
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Thư viện khác
    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3") // Dùng phiên bản mới nhất
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6") // Cập nhật phiên bản mới hơn
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")  // Cập nhật phiên bản mới hơn

    // Room Database
    implementation("androidx.room:room-runtime:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")

    // Android Debug Database
    debugImplementation("com.github.amitshekhariitbhu:Android-Debug-Database:1.0.6")

    // Loại bỏ xung đột Support Library
    configurations.all {
        exclude(group = "com.android.support", module = "support-compat")
    }

    //Thư viện Glide hoặc Picasso để tải ảnh
    implementation ("com.github.bumptech.glide:glide:4.16.0")


}