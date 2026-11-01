package Soal1

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class DashboardUiState(
    val joinedClubs: List<Community> = emptyList(),
    val projects: List<Project> = emptyList()
) {
    val joinedCount: Int get() = joinedClubs.size
    val totalProjects: Int get() = projects.size
    fun projectCountOf(communityId: String): Int = projects.count { it.communityId == communityId }
}

class CampusViewModel : ViewModel() {

    val profile = StudentProfile(
        name = "Aditya Pratama",
        major = "Teknik Informatika",
        batch = 2022,
        nim = "22001045"
    )

    val communities: List<Community> = SampleCommunities

    private val _joinedIds = MutableStateFlow(DefaultJoinedIds)
    val joinedIds: StateFlow<Set<String>> = _joinedIds.asStateFlow()

    private val _projects = MutableStateFlow(SampleProjects)
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    private val _form = MutableStateFlow(ProjectFormState())
    val form: StateFlow<ProjectFormState> = _form.asStateFlow()

    private val _events = MutableSharedFlow<CampusEvent>(extraBufferCapacity = 8)
    val events: SharedFlow<CampusEvent> = _events.asSharedFlow()

    private var nextProjectNumber = 3

    private fun buildDashboard(ids: Set<String>, projects: List<Project>) = DashboardUiState(
        joinedClubs = communities.filter { it.id in ids },
        projects = projects.filter { it.communityId in ids }
    )

    val dashboard: StateFlow<DashboardUiState> =
        combine(_joinedIds, _projects) { ids, projects -> buildDashboard(ids, projects) }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = buildDashboard(_joinedIds.value, _projects.value)
            )

    fun communityById(id: String): Community? = communities.find { it.id == id }

    fun toggleJoin(communityId: String) {
        _joinedIds.update { ids ->
            if (communityId in ids) ids - communityId else ids + communityId
        }
    }

    fun onCommunityClick(communityId: String) {
        if (communityId in _joinedIds.value) {
            _events.tryEmit(CampusEvent.OpenClub(communityId))
        } else {
            _events.tryEmit(CampusEvent.ShowMessage("Harap bergabung ke komunitas terlebih dahulu!"))
        }
    }

    fun showAddProject() {
        _form.value = ProjectFormState(isVisible = true)
    }

    fun updateTitle(title: String) {
        _form.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _form.update { it.copy(description = description) }
    }

    fun toggleTech(tech: String) {
        _form.update {
            val selected = if (tech in it.selectedTech) it.selectedTech - tech else it.selectedTech + tech
            it.copy(selectedTech = selected)
        }
    }

    fun dismissAddProject() {
        _form.value = ProjectFormState()
    }

    fun saveProject(communityId: String) {
        val draft = _form.value
        if (!draft.isValid || communityId !in _joinedIds.value) return

        val project = Project(
            id = "proj-$communityId-${nextProjectNumber.toString().padStart(2, '0')}",
            communityId = communityId,
            title = draft.title.trim(),
            description = draft.description.trim(),
            techStack = TECH_OPTIONS.filter { it in draft.selectedTech }
        )
        nextProjectNumber++
        _projects.update { it + project }
        _form.value = ProjectFormState()
    }

    fun deleteProject(projectId: String) {
        _projects.update { list -> list.filterNot { it.id == projectId } }
    }

    fun setProjectCompleted(projectId: String, completed: Boolean) {
        val newStatus = if (completed) ProjectStatus.COMPLETED else ProjectStatus.IN_PROGRESS
        _projects.update { list ->
            list.map { if (it.id == projectId) it.copy(status = newStatus) else it }
        }
    }
}