plugins {
    id("java")
    id("org.jetbrains.intellij") version "1.5.2"
}

group = "com.lyflexi"
version = "3.0.0"

repositories {
    mavenCentral()
}

// Configure Gradle IntelliJ Plugin - read more: https://github.com/JetBrains/gradle-intellij-plugin
intellij {
    version.set("2021.2")
    type.set("IU") // Target IDE Platform
    plugins.set(listOf("com.intellij.java"))
}

dependencies {
//    implementation("com.softwareloop:mybatis-generator-lombok-plugin:1.0")
    compileOnly("org.projectlombok:lombok:1.18.22")
//    annotationProcessor("org.projectlombok:lombok:1.18.2");
//    testAnnotationProcessor("org.projectlombok:lombok:1.18.2");
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        options.encoding = "UTF-8"
        sourceCompatibility = "11"
        targetCompatibility = "11"
    }

    withType<Javadoc>{
        options.encoding = "UTF-8"

    }

    patchPluginXml {
        sinceBuild.set("193")
//        支持更高版本的IDEA. 版本不设限
        untilBuild.set("")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }
    runIde {
        jvmArgs("-Xmx4096m","-XX:ReservedCodeCacheSize=512m","-Xms128m")
    }
}
