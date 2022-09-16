package anless.fleetmanagement.car_module.presentation.ui.comment

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.R
import anless.fleetmanagement.car_module.presentation.ui.CarActionViewModel
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.AndroidUtils
import anless.fleetmanagement.core.utils.observeInLifecycle
import anless.fleetmanagement.databinding.FragmentCommentBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach

@AndroidEntryPoint
class CommentFragment : Fragment(R.layout.fragment_comment) {

    companion object {
        const val TAG = "CommentFragment"
    }

    private val commentViewModel: CommentViewModel by viewModels()
    private val carActionViewModel: CarActionViewModel by activityViewModels()
    private var _binding: FragmentCommentBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCommentBinding.bind(view)

        AndroidUtils.openKeyboard(binding.etComment)

        binding.etComment.addTextChangedListener { comment ->
            commentViewModel.setComment(comment.toString())
        }

        binding.btnNext.setOnClickListener {
            carActionViewModel.setComment(commentViewModel.getComment())
        }

        val resTitle = carActionViewModel.getActionTitle()
        resTitle?.let { title ->
            (activity as MainActivity).setTitle(getString(title))
        }

        carActionViewModel.getCarLicensePlate()?.let { licensePlate ->
            (activity as MainActivity).setSubTitle(licensePlate.uppercase())
        }

        commentViewModel.setCommentRequireState(carActionViewModel.getCommentRequire())

        subscribeUi()
    }

    private fun subscribeUi() {
/*        lifecycleScope.launchWhenStarted {
            commentViewModel.comment.collectLatest { comment ->
                binding.btnNext.isEnabled = (comment.isNotBlank())
            }
        }*/

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            carActionViewModel.idCar.collectLatest { idCar ->
                if (idCar == null) {
                    findNavController().popBackStack()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            commentViewModel.commentIsReady.collectLatest { isReady ->
                binding.btnNext.isEnabled = isReady
            }
        }

        carActionViewModel.navigateNext.onEach { nextScreen ->
            findNavController().navigate(nextScreen)
        }.observeInLifecycle(viewLifecycleOwner)
    }

    override fun onResume() {
        super.onResume()
        carActionViewModel.setCurrentScreen(findNavController().currentDestination?.id)
    }
}