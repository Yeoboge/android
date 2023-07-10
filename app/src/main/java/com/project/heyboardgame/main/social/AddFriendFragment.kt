package com.project.heyboardgame.main.social

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import com.project.heyboardgame.R
import com.project.heyboardgame.databinding.FragmentAddFriendBinding


class AddFriendFragment : Fragment(R.layout.fragment_add_friend) {

    // View Binding
    private var _binding : FragmentAddFriendBinding? = null
    private val binding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddFriendBinding.bind(view)

        binding.apply {
            nicknameSearchView.isSubmitButtonEnabled = true
        }
    }


}