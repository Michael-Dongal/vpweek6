package Soal1

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.coroutines.launch

private class TopLevelRoute<T : Any>(
    val label: String,
    val route: T,
    val icon: ImageVector
)

private val topLevelRoutes = listOf(
    TopLevelRoute("Dashboard", DashboardRoute, Icons.Filled.Home),
    TopLevelRoute("Communities", CommunitiesRoute, Icons.Filled.Group)
)

/**
 * App root: Scaffold + Bottom Bar + type-safe NavHost.
 * One [CampusViewModel] (scoped to the Activity) is shared by every screen.
 */
@Composable
fun CampusApp(viewModel: CampusViewModel = viewModel()) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // One-shot events from the ViewModel: gate-access message and navigation to a club.
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is CampusEvent.ShowMessage -> {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = event.message,
                            actionLabel = "OK",
                            duration = SnackbarDuration.Short
                        )
                    }
                }

                is CampusEvent.OpenClub -> {
                    navController.navigate(ClubProjectsRoute(event.communityId)) {
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = backStackEntry?.destination
    // Bottom bar is shown only on the two top-level tabs.
    val showBottomBar = topLevelRoutes.any { item ->
        currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelRoutes.forEach { item ->
                        val selected = currentDestination?.hierarchy?.any {
                            it.hasRoute(item.route::class)
                        } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                // Save & restore state so lists, forms and scroll positions survive tab switches.
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DashboardRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<DashboardRoute> {
                val dashboard by viewModel.dashboard.collectAsState()
                DashboardScreen(
                    profile = viewModel.profile,
                    state = dashboard,
                    onClubClick = viewModel::onCommunityClick,
                    onProjectClick = { projectId ->
                        navController.navigate(ProjectDetailRoute(projectId))
                    }
                )
            }

            composable<CommunitiesRoute> {
                val joinedIds by viewModel.joinedIds.collectAsState()
                CommunitiesScreen(
                    communities = viewModel.communities,
                    joinedIds = joinedIds,
                    onToggleJoin = viewModel::toggleJoin,
                    onCommunityClick = viewModel::onCommunityClick
                )
            }

            composable<ClubProjectsRoute> { entry ->
                val route = entry.toRoute<ClubProjectsRoute>()
                val allProjects by viewModel.projects.collectAsState()
                val form by viewModel.form.collectAsState()
                ClubProjectsScreen(
                    community = viewModel.communityById(route.communityId),
                    projects = allProjects.filter { it.communityId == route.communityId },
                    form = form,
                    onBack = { navController.popBackStack() },
                    onProjectClick = { projectId ->
                        navController.navigate(ProjectDetailRoute(projectId))
                    },
                    onDeleteProject = viewModel::deleteProject,
                    onAddClick = viewModel::showAddProject,
                    onTitleChange = viewModel::updateTitle,
                    onDescriptionChange = viewModel::updateDescription,
                    onToggleTech = viewModel::toggleTech,
                    onCancelForm = viewModel::dismissAddProject,
                    onSaveForm = { viewModel.saveProject(route.communityId) }
                )
            }

            composable<ProjectDetailRoute> { entry ->
                val route = entry.toRoute<ProjectDetailRoute>()
                val allProjects by viewModel.projects.collectAsState()
                val project = allProjects.find { it.id == route.projectId }
                ProjectDetailScreen(
                    project = project,
                    community = project?.let { viewModel.communityById(it.communityId) },
                    onBack = { navController.popBackStack() },
                    onStatusChange = { completed ->
                        viewModel.setProjectCompleted(route.projectId, completed)
                    }
                )
            }
        }
    }
}