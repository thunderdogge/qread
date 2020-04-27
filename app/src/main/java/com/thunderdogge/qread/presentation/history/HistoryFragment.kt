package com.thunderdogge.qread.presentation.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thunderdogge.qread.BR
import com.thunderdogge.qread.R
import com.thunderdogge.qread.databinding.FragmentHistoryBinding
import com.thunderdogge.qread.presentation.base.BaseFragment
import com.thunderdogge.qread.presentation.extensions.showSnackbar
import kotlinx.android.synthetic.main.fragment_history.*
import me.tatarka.bindingcollectionadapter2.ItemBinding

class HistoryFragment : BaseFragment() {

    private val viewModel by viewModel<HistoryViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        val binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.vm = viewModel
        binding.binding = ItemBinding.of { itemBinding, _, itemViewModel ->
            when (itemViewModel) {
                is HistoryEntityViewModel.Item -> itemBinding.set(BR.item, R.layout.view_item_history_item).bindExtra(BR.parent, viewModel)
                is HistoryEntityViewModel.Group -> itemBinding.set(BR.item, R.layout.view_item_history_group)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObservers()
        initAppearance()
    }

    private fun initObservers() {
        viewModel.snackbarMessage.observe(viewLifecycleOwner, Observer(::showSnackbar))
        viewModel.clearHistoryPromptDialog.observe(viewLifecycleOwner, Observer { showClearHistoryPromptDialog() })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_history, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuHistoryDelete) {
            viewModel.promptClearHistory()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    private fun initAppearance() {
        setSupportActionBar(toolbar)
        setDisplayHomeAsUpEnabled()
    }

    private fun showClearHistoryPromptDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.history_delete_dialog_title)
            .setMessage(R.string.history_delete_dialog_text)
            .setPositiveButton(R.string.history_delete_dialog_positive) { _, _ -> viewModel.clearHistory() }
            .setNegativeButton(R.string.dialog_common_button_cancel, null)
            .show()
    }

    companion object {
        fun newInstance(): Fragment {
            return HistoryFragment()
        }
    }
}