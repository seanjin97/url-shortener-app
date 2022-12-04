plugins {
    id("org.jetbrains.kotlin.plugin.jpa")
}

//ext {
//    val devDependencies = {
//       runtime "com.h2database:h2")
//    }
//}
dependencies {
    implementation(project(":models"))
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.0")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")
    implementation("org.apache.commons:commons-lang3:3.12.0")
}