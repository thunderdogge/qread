package com.thunderdogge.qread.presentation.history

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thunderdogge.qread.R
import com.thunderdogge.qread.databinding.FragmentHistoryBinding
import com.thunderdogge.qread.presentation.base.BaseFragment

class HistoryFragment : BaseFragment(R.layout.fragment_history) {

    private lateinit var adapter: HistoryAdapter

    private val viewModel by viewModel<HistoryViewModel>()

    private val viewBinding by viewBinding<FragmentHistoryBinding>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapter()
        setupBinding()
        setupAppearance()
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

    private fun setupAdapter() {
        adapter = HistoryAdapter {
            viewModel.onHistoryItemClick(it)
        }
        viewBinding.rvHistory.adapter = adapter
    }

    private fun setupBinding() {
        viewModel.entities.observe(viewLifecycleOwner, { handleEntities(it) })
        viewModel.isLoading.observe(viewLifecycleOwner, { viewBinding.pbLoading.isVisible = it })
        viewModel.clearHistoryPromptDialog.observe(viewLifecycleOwner, { showClearHistoryPromptDialog() })
    }

    private fun setupAppearance() {
        setSupportActionBar(viewBinding.toolbar)
        setDisplayHomeAsUpEnabled()
    }

    private fun handleEntities(entities: List<HistoryEntityViewModel>) {
        adapter.items = entities
        adapter.notifyDataSetChanged()
        viewBinding.llEmpty.isVisible = entities.isEmpty()
    }

    private fun showClearHistoryPromptDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.history_delete_dialog_title)
            .setMessage(R.string.history_delete_dialog_text)
            .setPositiveButton(R.string.history_delete_dialog_positive) { _, _ -> viewModel.onHistoryClearClick() }
            .setNegativeButton(R.string.dialog_common_button_cancel, null)
            .show()
    }

    companion object {
        fun newInstance(): Fragment {
            return HistoryFragment()
        }
    }
}
