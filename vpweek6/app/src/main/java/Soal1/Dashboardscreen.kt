package Soal1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Screen 1: Profile Dashboard. Stateless: everything comes in as parameters. */
@Composable
fun DashboardScreen(
    profile: StudentProfile,
    state: DashboardUiState,
    onClubClick: (String) -> Unit,
    onProjectClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState()
) {
    val clubById = remember(state.joinedClubs) { state.joinedClubs.associateBy { it.id } }

    Column(modifier = modifier.fillMaxSize()) {
        ScreenHeader(
            title = "CampusTech Hub",
            subtitle = "Student Profile & Portfolio Dashboard",
            trailing = { AvatarCircle(initials = profile.initials, size = 40.dp) }
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { ProfileCard(profile) }

            item {
                StatsRow(
                    joinedCount = state.joinedCount,
                    projectCount = state.totalProjects
                )
            }

            item {
                SectionTitle(
                    icon = Icons.Filled.Star,
                    iconTint = MaterialTheme.colorScheme.primary,
                    title = "Semua Komunitas yang Diikuti (${state.joinedCount})",
                    modifier = Modifier.padding(top = 8.dp),
                    trailing = "Status: Joined"
                )
            }
            if (state.joinedClubs.isEmpty()) {
                item { EmptyMessage("Belum bergabung dengan komunitas") }
            } else {
                items(state.joinedClubs, key = { it.id }) { club ->
                    JoinedClubRow(
                        community = club,
                        projectCount = state.projectCountOf(club.id),
                        onClick = { onClubClick(club.id) }
                    )
                }
            }

            item {
                SectionTitle(
                    icon = Icons.Filled.Layers,
                    iconTint = Color(0xFF6D28D9),
                    title = "Semua Proyek Mahasiswa (${state.totalProjects})",
                    modifier = Modifier.padding(top = 8.dp),
                    trailing = "Total: ${state.totalProjects} Proyek"
                )
            }
            if (state.projects.isEmpty()) {
                item { EmptyMessage("Belum ada proyek yang dicatat") }
            } else {
                items(state.projects, key = { it.id }) { project ->
                    DashboardProjectCard(
                        project = project,
                        community = clubById[project.communityId],
                        onClick = { onProjectClick(project.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileCard(profile: StudentProfile) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.linearGradient(listOf(Color(0xFF1E40AF), Color(0xFF3B82F6))))
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = profile.initials,
                color = Color(0xFF1E40AF),
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp
            )
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Surface(shape = CircleShape, color = Color.White.copy(alpha = 0.18f)) {
                Text(
                    text = "MAHASISWA AKTIF",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    color = Color.White,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = profile.name,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${profile.major} • Angkatan ${profile.batch}",
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "NIM: ${profile.nim}",
                color = Color.White.copy(alpha = 0.75f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun StatsRow(joinedCount: Int, projectCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Group,
            iconTint = Color(0xFF1D4ED8),
            iconBackground = Color(0xFFDBEAFE),
            count = joinedCount,
            label = "Joined Clubs",
            caption = "Komunitas yang Diikuti"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Filled.Layers,
            iconTint = Color(0xFF6D28D9),
            iconBackground = Color(0xFFEDE9FE),
            count = projectCount,
            label = "Total Projects",
            caption = "Seluruh Proyek Dibuat"
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    iconTint: Color,
    iconBackground: Color,
    count: Int,
    label: String,
    caption: String,
    modifier: Modifier = Modifier
) {
    CampusCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconBackground, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = count.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = caption,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun JoinedClubRow(
    community: Community,
    projectCount: Int,
    onClick: () -> Unit
) {
    CampusCard(onClick = onClick, shape = RoundedCornerShape(20.dp)) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommunityIconBox(community)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = community.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${community.subtitle} • $projectCount Proyek",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.width(8.dp))
            JoinedBadge()
        }
    }
}

@Composable
private fun DashboardProjectCard(
    project: Project,
    community: Community?,
    onClick: () -> Unit
) {
    CampusCard(onClick = onClick, shape = RoundedCornerShape(20.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (community != null) {
                    CommunityIconBox(community)
                    Spacer(Modifier.width(12.dp))
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = community?.name.orEmpty(),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = project.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = project.techStack.joinToString(" • "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.outlineVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                StatusBadge(project.status)
            }
        }
    }
}