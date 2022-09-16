package anless.fleetmanagement.car_module.presentation.ui.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor() : ViewModel() {

    companion object {
        const val TAG = "CommentViewModel"
        const val COMMENT_MIN_LENGTH = 3
    }

    private val _comment = MutableStateFlow<String>(value = "")
    val comment = _comment.asStateFlow()

    private val isCommentRequire = MutableStateFlow(value = true)

    val commentIsReady: StateFlow<Boolean> =
        combine(_comment, isCommentRequire) { comment, isRequire ->
        if (isRequire) {
            comment.trim().length >= COMMENT_MIN_LENGTH
        } else true
    }.stateIn(viewModelScope, SharingStarted.Eagerly, initialValue = false)

    fun setCommentRequireState(isRequire: Boolean) {
        isCommentRequire.value = isRequire
    }

    fun setComment(comment: String) {
        _comment.value = comment
    }

    fun getComment() = _comment.value

}