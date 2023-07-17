package com.project.heyboardgame.auth

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.project.heyboardgame.R
import com.project.heyboardgame.databinding.FragmentSignUp1Binding


class SignUpFragment1 : Fragment() {

    // View Binding
    private var _binding : FragmentSignUp1Binding? = null
    private val binding get() = _binding!!
    // View Model
    private lateinit var authViewModel: AuthViewModel
    // 각 에러 상태를 저장하는 변수들
    private var isPasswordCheckFail = false
    private var isNicknameInvalid = false
    private var isEmailInvalid = false
    private var isEmailDuplicated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentSignUp1Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.password.addTextChangedListener(passwordTextWatcher) // 비밀번호 입력창에 TextWatcher 추가
        binding.passwordCheck.addTextChangedListener(passwordCheckTextWatcher) // 비밀번호 확인 입력창에 TextWatcher 추가
        binding.nickname.addTextChangedListener(nicknameTextWatcher) // 닉네임 입력창에 TextWatcher 추가
        binding.email.addTextChangedListener(emailTextWatcher) // 이메일 입력창에 TextWatcher 추가

        // 이메일 중복 확인 버튼 클릭 시 이벤트
        binding.emailCheck.setOnClickListener {
            val email = binding.email.text.toString()
            authViewModel.checkDuplicateEmail(
                email,
                onSuccess = {
                    // 이메일 중복이 아닌 경우
                    binding.emailCheckSuccess.visibility = View.VISIBLE
                    binding.emailCheckFail.visibility = View.GONE
                    isEmailDuplicated = false
                    updateNextButtonState()
                },
                onFailure = {
                    // 이메일 중복인 경우 또는 실패한 경우
                    binding.emailCheckSuccess.visibility = View.GONE
                    binding.emailCheckFail.visibility = View.VISIBLE
                    isEmailDuplicated = true
                    updateNextButtonState()
                }
            )
        }

        // 다음 단계 버튼 누를 때 발생하는 이벤트
        binding.nextBtn.setOnClickListener {
            if (!isPasswordCheckFail && !isNicknameInvalid && !isEmailInvalid && !isEmailDuplicated) {
                // 모든 에러가 없는 경우에만 다음 단계로 이동
                Navigation.findNavController(view).navigate(R.id.action_signUpFragment1_to_signUpFragment2)
            }
        }

        // 뒤로가기 아이콘 누를 때 발생하는 이벤트
        binding.backBtn.setOnClickListener {
            Navigation.findNavController(view).popBackStack()
        }
    }

    // 이메일 TextWatcher
    private val emailTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateEmail(s.toString())
        }
        override fun afterTextChanged(s: Editable?) {
            validateEmail(s.toString())
            binding.emailCheckSuccess.visibility = View.GONE
            binding.emailCheckFail.visibility = View.GONE
            isEmailDuplicated = true
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 생략
        }
    }
    // 비밀번호 TextWatcher
    private val passwordTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validatePasswordCheck()
        }
        override fun afterTextChanged(s: Editable?) {
            validatePasswordCheck()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 생략
        }
    }
    // 비밀번호 확인 TextWatcher
    private val passwordCheckTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validatePasswordCheck()
        }
        override fun afterTextChanged(s: Editable?) {
            validatePasswordCheck()
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 생략
        }
    }
    // 닉네임 TextWatcher
    private val nicknameTextWatcher = object : TextWatcher {
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validateNickname(s.toString())
        }
        override fun afterTextChanged(s: Editable?) {
            validateNickname(s.toString())
        }
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 생략
        }
    }
    // 이메일 형식 유효성 검사하는 함수
    private fun validateEmail(email: String) {
        val regex = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+") // 이메일 형식 검사용 정규식
        val isInvalidFormat = !regex.matches(email)

        if (isInvalidFormat) {
            binding.emailInvalid.visibility = View.VISIBLE
            binding.emailCheck.isEnabled = false
            isEmailInvalid = true
        } else {
            binding.emailInvalid.visibility = View.GONE
            binding.emailCheck.isEnabled = true
            isEmailInvalid = false
        }
        updateNextButtonState()
    }
    // 비밀번호와 비밀번호 확인이 같은 지 체크하는 함수
    private fun validatePasswordCheck() {
        val password = binding.password.text.toString()
        val passwordCheck = binding.passwordCheck.text.toString()
        val isMatch = password == passwordCheck

        if (isMatch) {
            // 비밀번호 확인이 일치하는 경우
            binding.passwordCheckFail.visibility = View.GONE
            isPasswordCheckFail = false
        } else {
            // 비밀번호 확인이 일치하지 않는 경우
            binding.passwordCheckFail.visibility = View.VISIBLE
            isPasswordCheckFail = true
        }
        updateNextButtonState()
    }
    // 닉네임 유효성 검사하는 함수
    private fun validateNickname(nickname: String) {
        val regex = Regex("^[A-Za-z0-9가-힣]+$") // 영문 대소문자, 숫자, 한글만 허용
        val containsSpecialCharacters = !regex.matches(nickname)

        if (containsSpecialCharacters) {
            binding.nicknameInvalid.visibility = View.VISIBLE
            isNicknameInvalid = true
        } else {
            binding.nicknameInvalid.visibility = View.GONE
            isNicknameInvalid = false
        }
        updateNextButtonState()
    }
    // 모든 입력창에 값을 입력했는 지 확인하는 함수
    private fun isInputValid(): Boolean {
        val password = binding.password.text.toString()
        val passwordCheck = binding.passwordCheck.text.toString()
        val nickname = binding.nickname.text.toString()
        val email = binding.email.text.toString()

        return password.isNotEmpty() && passwordCheck.isNotEmpty() && nickname.isNotEmpty() && email.isNotEmpty()
    }

    // 다음 버튼 상태 업데이트
    private fun updateNextButtonState() {
        binding.nextBtn.isEnabled = isInputValid() && !isPasswordCheckFail && !isNicknameInvalid && !isEmailInvalid && !isEmailDuplicated
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}