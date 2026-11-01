package Soal1

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class StudentProfile(
    val name: String,
    val major: String,
    val batch: Int,
    val nim: String
) {
    val initials: String
        get() = name.split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
}

data class Community(
    val id: String,
    val name: String,
    val subtitle: String,
    val members: Int,
    val description: String,
    val icon: ImageVector,
    val accent: Color,
    val accentBackground: Color
)

enum class ProjectStatus(val label: String) {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed")
}

data class Project(
    val id: String,
    val communityId: String,
    val title: String,
    val description: String,
    val techStack: List<String>,
    val status: ProjectStatus = ProjectStatus.IN_PROGRESS
)

data class ProjectFormState(
    val isVisible: Boolean = false,
    val title: String = "",
    val description: String = "",
    val selectedTech: Set<String> = emptySet()
) {
    val isValid: Boolean
        get() = title.isNotBlank() && description.isNotBlank() && selectedTech.isNotEmpty()
}

sealed interface CampusEvent {
    data class ShowMessage(val message: String) : CampusEvent
    data class OpenClub(val communityId: String) : CampusEvent
}

val TECH_OPTIONS = listOf(
    "Kotlin",
    "Jetpack Compose",
    "Firebase",
    "Ktor",
    "Room",
    "Python",
    "Google Maps SDK",
    "CameraX"
)

val SampleCommunities = listOf(
    Community(
        id = "gdsc",
        name = "GDSC",
        subtitle = "Google Developer Student Club",
        members = 142,
        description = "Fokus pada ekosistem Google: Android Compose, Flutter, Firebase, dan Google Cloud Platform.",
        icon = Icons.Filled.Code,
        accent = Color(0xFF1D4ED8),
        accentBackground = Color(0xFFDBEAFE)
    ),
    Community(
        id = "cyber",
        name = "CyberSecurity Guild",
        subtitle = "Security & Pentesting",
        members = 88,
        description = "Eksplorasi ethical hacking, CTF challenges, reverse engineering, dan pertahanan jaringan kampus.",
        icon = Icons.Filled.Security,
        accent = Color(0xFFDC2626),
        accentBackground = Color(0xFFFEE2E2)
    ),
    Community(
        id = "ai",
        name = "AI Society",
        subtitle = "Machine Learning & LLM",
        members = 115,
        description = "Riset terapan deep learning, computer vision, agen cerdas, dan fine-tuning large language models.",
        icon = Icons.Filled.AutoAwesome,
        accent = Color(0xFF7C3AED),
        accentBackground = Color(0xFFEDE9FE)
    ),
    Community(
        id = "mobile",
        name = "Mobile Dev Community",
        subtitle = "Native Jetpack Compose & iOS",
        members = 95,
        description = "Membangun aplikasi mobile native: Jetpack Compose, Kotlin Multiplatform, dan SwiftUI.",
        icon = Icons.Filled.PhoneAndroid,
        accent = Color(0xFF0284C7),
        accentBackground = Color(0xFFE0F2FE)
    ),
    Community(
        id = "cloud",
        name = "Cloud & DevOps Society",
        subtitle = "Cloud Native & CI/CD",
        members = 76,
        description = "Belajar deployment, containerization, CI/CD pipeline, dan infrastruktur cloud skala kampus.",
        icon = Icons.Filled.Cloud,
        accent = Color(0xFF0D9488),
        accentBackground = Color(0xFFCCFBF1)
    ),
    Community(
        id = "game",
        name = "Game Dev Studio",
        subtitle = "Game Design & Unity",
        members = 64,
        description = "Merancang dan mengembangkan game bersama: game design, Unity, dan pixel art.",
        icon = Icons.Filled.SportsEsports,
        accent = Color(0xFFEA580C),
        accentBackground = Color(0xFFFFEDD5)
    )
)

val DefaultJoinedIds = setOf("gdsc", "cyber", "mobile")

val SampleProjects = listOf(
    Project(
        id = "proj-gdsc-01",
        communityId = "gdsc",
        title = "Smart Campus Navigation",
        description = "Sistem navigasi peta gedung kampus interaktif berbasis Jetpack Compose dan Google Maps SDK. " +
                "Memfasilitasi mahasiswa dan dosen menemukan ruang perkuliahan, laboratorium komputer, perpustakaan, " +
                "serta fasilitas umum kampus secara akurat disertai rute tercepat dan visualisasi lokasi real-time.",
        techStack = listOf("Kotlin", "Jetpack Compose", "Room", "Google Maps SDK"),
        status = ProjectStatus.IN_PROGRESS
    ),
    Project(
        id = "proj-gdsc-02",
        communityId = "gdsc",
        title = "Student Attendance QR Scanner",
        description = "Pemindai barcode & QR presensi kehadiran workshop berbasis CameraX & Firebase.",
        techStack = listOf("Jetpack Compose", "Firebase", "CameraX"),
        status = ProjectStatus.COMPLETED
    ),
    Project(
        id = "proj-cyber-01",
        communityId = "cyber",
        title = "CTF Writeup Tracker",
        description = "Aplikasi pencatat writeup dan skor tantangan CTF untuk anggota CyberSecurity Guild.",
        techStack = listOf("Kotlin", "Room", "Python"),
        status = ProjectStatus.IN_PROGRESS
    ),
    Project(
        id = "proj-mobile-01",
        communityId = "mobile",
        title = "Developer RSVP Portal",
        description = "Portal pendaftaran webinar dan bootcamp teknologi dengan sinkronisasi Ktor API server.",
        techStack = listOf("Kotlin", "Ktor", "Jetpack Compose"),
        status = ProjectStatus.IN_PROGRESS
    )
)