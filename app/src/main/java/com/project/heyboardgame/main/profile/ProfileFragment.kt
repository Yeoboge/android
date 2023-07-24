package com.project.heyboardgame.main.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.project.heyboardgame.R
import com.project.heyboardgame.adapter.BadgeRVAdapter
import com.project.heyboardgame.auth.AuthActivity
import com.project.heyboardgame.dataStore.MyDataStore
import com.project.heyboardgame.databinding.FragmentProfileBinding
import com.project.heyboardgame.main.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ProfileFragment : Fragment() {

    // 뒤로 가기 이벤트를 위한 변수
    private lateinit var callback : OnBackPressedCallback
    private var backPressedTime : Long = 0

    // View Binding
    private var _binding : FragmentProfileBinding? = null
    private val binding get() = _binding!!
    // Adapter
    private lateinit var badgeRVAdapter: BadgeRVAdapter
    // View Model
    private lateinit var mainViewModel: MainViewModel
    // DataStore
    private val myDataStore : MyDataStore = MyDataStore()

    // 화면에서 뒤로 가기를 두 번 눌렀을 때 종료시켜주는 함수
    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (System.currentTimeMillis() - backPressedTime < 2500) {
                    activity?.finish()
                    return
                }
                Toast.makeText(activity, "한 번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
                backPressedTime = System.currentTimeMillis()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val profileImg = withContext(Dispatchers.IO) {
                myDataStore.getProfileImage()
            }
            val nickname = withContext(Dispatchers.IO) {
                myDataStore.getNickname()
            }
            val friendCode = withContext(Dispatchers.IO) {
                myDataStore.getFriendCode()
            }
            if (profileImg != null) {
                Glide.with(requireContext())
                    .load(profileImg)
                    .into(binding.myProfileImg)
            } else {
                binding.myProfileImg.setImageResource(R.drawable.default_profile_img)
            }
            binding.myNickname.text = nickname
            binding.myFriendCode.text = friendCode
        }

        var badgeList = mutableListOf<String>()
        badgeList.add("뱃지")
        badgeList.add("뱃지")
        badgeList.add("뱃지")

        badgeRVAdapter = BadgeRVAdapter(requireContext(), badgeList)
        binding.badgeRV.adapter = badgeRVAdapter
        binding.badgeRV.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.apply {
            logout.setOnClickListener {
                mainViewModel.requestLogout(
                    onSuccess = {
                        val intent = Intent(requireContext(), AuthActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)

                        Toast.makeText(requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                    },
                    onFailure = {
                        Toast.makeText(requireContext(), "로그아웃 실패", Toast.LENGTH_SHORT).show()
                    },
                    onErrorAction = {
                        Toast.makeText(requireContext(), "네트워크 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                    }
                )
            }
            changeProfile.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_changeProfileFragment)
            }
            changePassword.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_changePwdFragment)
            }
            unregister.setOnClickListener {
                findNavController().navigate(R.id.action_profileFragment_to_unregisterFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // 뒤로 가기 두 번을 위해 추가
    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}