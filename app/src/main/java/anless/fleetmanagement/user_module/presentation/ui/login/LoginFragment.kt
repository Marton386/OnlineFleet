package anless.fleetmanagement.user_module.presentation.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import anless.fleetmanagement.BuildConfig
import anless.fleetmanagement.R
import anless.fleetmanagement.core.app.presentation.ui.MainActivity
import anless.fleetmanagement.core.utils.AndroidUtils
import anless.fleetmanagement.databinding.FragmentLoginBinding
import anless.fleetmanagement.core.app.presentation.utils.showErrorDialog
import com.github.razir.progressbutton.bindProgressButton
import com.github.razir.progressbutton.hideProgress
import com.github.razir.progressbutton.showProgress
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {

    companion object {
        private const val TAG = "LoginFragment"
    }

    private val loginViewModel: LoginViewModel by viewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentLoginBinding.bind(view)

        bindProgressButton(binding.btnLogin)

        AndroidUtils.openKeyboard(binding.etLogin)

        binding.etLogin.addTextChangedListener { text ->
            loginViewModel.setLogin(text.toString())
        }

        binding.etPassword.addTextChangedListener { text ->
            loginViewModel.setPassword(text.toString())
        }

        binding.btnLogin.setOnClickListener {
            loginViewModel.loginUser(BuildConfig.VERSION_CODE)
        }

        (activity as MainActivity).setTitle(getString(R.string.app_name))
        (activity as MainActivity).setSubTitle(null)

        subscribeUi()
    }

    private fun subscribeUi() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            loginViewModel.state.collectLatest { state ->
                when (state) {
                    is LoginViewModel.LoginState.Success -> {
                        binding.btnLogin.hideProgress(R.string.login)
                        findNavController().navigateUp()
                    }
                    is LoginViewModel.LoginState.Error -> {
                        binding.btnLogin.hideProgress(R.string.login)
                        showErrorDialog(error = getString(state.codeError))
                    }
                    is LoginViewModel.LoginState.Loading -> {
                        binding.btnLogin.showProgress()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            loginViewModel.readyToLogin.collectLatest { isReady ->
                binding.btnLogin.isEnabled = isReady
            }
        }
    }


}