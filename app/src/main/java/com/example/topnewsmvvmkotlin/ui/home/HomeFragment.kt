package com.example.topnewsmvvmkotlin.ui.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.topnewsmvvmkotlin.R
import com.example.topnewsmvvmkotlin.ui.adapter.ArticlesAdapterRecyclerView
import com.example.topnewsmvvmkotlin.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(), ArticlesAdapterRecyclerView.OnClickSelectedItem {

    private val homeViewModel: HomeViewModel by viewModel() //inyección de dependencia
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var adapterRecycler: ArticlesAdapterRecyclerView =
        ArticlesAdapterRecyclerView(mutableListOf(), this)

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        setQuery(arguments)


        if (auth.currentUser == null) {
            findNavController().apply {
                popBackStack()
                navigate(R.id.loginFragment)
            }

        }
        homeViewModel.modelArticles.observe(this, Observer(::upDateUi))

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {


        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        onScrollTopNews()
    }

    override fun onResume() {
        super.onResume()
        val bottomNavView = activity?.findViewById<BottomNavigationView>(R.id.navBottomNavigation)
        val titleApp = activity?.findViewById<TextView>(R.id.title)
        bottomNavView?.show()
        titleApp?.show()
    }

    private fun upDateUi(state: HomeViewModel.StateLiveData) {
        when (state) {
            is HomeViewModel.StateLiveData.InitialStateUi -> {
                homeViewModel.getDataArticles()
            }
            is HomeViewModel.StateLiveData.PreCall -> {
                progressBar.show()
            }

            is HomeViewModel.StateLiveData.RefreshStateUi -> {

                if (homeViewModel.getTotalResults() == 0) {
                    findNavController().popBackStack(R.id.filtersFragment, false)
                    makeToast(context, getString(R.string.noResults))
                }
                adapterRecycler.addData(state.response)
            }
            is HomeViewModel.StateLiveData.PostCall -> {
                progressBar.hide()
            }

            is HomeViewModel.StateLiveData.AdapterRecycler -> {
                for (data in state.dataRecyclerView)
                    adapterRecycler.addData(data)
                topNews_recyclerView.layoutManager?.scrollToPosition(homeViewModel.pos.minus(1))
            }
        }
    }

    private fun setupRecyclerView() {
        linearLayoutManager = LinearLayoutManager(requireContext())
        topNews_recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = adapterRecycler
        }
    }

    private fun onScrollTopNews() {
        topNews_recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                homeViewModel.pos = adapterRecycler.getPosition()
                if (topNews_recyclerView.isLastArticleDisplayed(linearLayoutManager)) {
                    if (homeViewModel.page++ * Constants.PAGESIZE < homeViewModel.getTotalResults()) {
                        homeViewModel.getDataArticles()

                    } else {
                        makeToast(context, getString(R.string.allArticlesdisplayed))
                    }
                }
            }
        })
    }

    private fun setQuery(args: Bundle?) {
        if (args != null) homeViewModel.queryFilters = HomeFragmentArgs.fromBundle(args)
            .defaulValuesFilter.split(",")
    }

    override fun onClick(query: String) {
        val passUrl = HomeFragmentDirections.actionHomeFragmentToDeepLinkFragment(query)
        findNavController().navigate(passUrl)
    }
}
