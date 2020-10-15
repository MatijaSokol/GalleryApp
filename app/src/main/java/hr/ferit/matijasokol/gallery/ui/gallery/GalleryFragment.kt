package hr.ferit.matijasokol.gallery.ui.gallery

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import dagger.hilt.android.AndroidEntryPoint
import hr.ferit.matijasokol.gallery.R
import hr.ferit.matijasokol.gallery.models.UnsplashPhoto
import hr.ferit.matijasokol.gallery.databinding.FragmentGalleryBinding
import kotlinx.android.synthetic.main.fragment_gallery.*

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val viewModel by viewModels<GalleryViewModel>()

    private val adapter by lazy { UnsplashPhotoAdapter { onItemClicked(it) } }

    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentGalleryBinding.bind(view)

        setRecycler()
        setObservers()

        btnRetry.setOnClickListener {
            adapter.retry()
        }

        adapter.addLoadStateListener {  loadState ->
            with(binding) {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                btnRetry.isVisible = loadState.source.refresh is LoadState.Error
                tvError.isVisible = loadState.source.refresh is LoadState.Error

                // empty view
                if (loadState.source.refresh is LoadState.NotLoading &&
                        loadState.append.endOfPaginationReached &&
                        adapter.itemCount < 1) {
                    recyclerView.isVisible = false
                    tvEmpty.isVisible = true
                } else {
                    tvEmpty.isVisible = false
                }
            }
        }

        setHasOptionsMenu(true)
    }

    private fun setObservers() {
        viewModel.photos.observe(viewLifecycleOwner, Observer {
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        })
    }

    private fun setRecycler() {
        with(binding) {
            recyclerView.setHasFixedSize(true)
            recyclerView.itemAnimator = null
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter { adapter.retry() },
                footer = UnsplashPhotoLoadStateAdapter { adapter.retry() }
            )
        }
    }

    private fun onItemClicked(photo: UnsplashPhoto) {
        val action = GalleryFragmentDirections.actionGalleryFragmentToDetailsFragment(photo, viewModel.currentQuery.value!!)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.menu_gallery, menu)
        val searchItem = menu.findItem(R.id.actionSearch)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchPhotos(it)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?) = true
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}