// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}

// Đảm bảo tất cả các module sử dụng repositories từ settings.gradle.kts
allprojects {
    repositories {
        // Không cần định nghĩa lại nếu đã dùng dependencyResolutionManagement trong settings.gradle.kts
        // Nhưng để an toàn, có thể thêm google() và mavenCentral() nếu cần
    }
}

// Tùy chọn: Thêm task clean nếu chưa có
tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
