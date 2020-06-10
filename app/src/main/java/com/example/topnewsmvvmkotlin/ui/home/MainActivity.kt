package com.example.topnewsmvvmkotlin.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.topnewsmvvmkotlin.R
import com.example.topnewsmvvmkotlin.data.model.ModelResponse
import com.example.topnewsmvvmkotlin.ui.adapter.ArticlesAdapterRecyclerView
import com.example.topnewsmvvmkotlin.ui.browser.WebActivity
import com.example.topnewsmvvmkotlin.util.Constants
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(),
    ArticlesAdapterRecyclerView.OnClickSelectedItem {

    private val homeViewModel: HomeViewModel by viewModel()  //inyecciòn de dependencia
    private lateinit var listAdapter: ArticlesAdapterRecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        homeViewModel.getDataArticles.observe(this, Observer(::upDateUi)) //.Observer {upDate(it}
    }
    private fun upDateUi(data: ModelResponse) {

        listAdapter = ArticlesAdapterRecyclerView(mutableListOf(), this)
        listAdapter.addData(data)
        setupRecyclerView()
    }
    private fun setupRecyclerView() {
        topNews_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }
    }
    override fun onClick(query: String) {
        intent = Intent(this, WebActivity::class.java)
        intent.putExtra(Constants.KEYURL_INTENT, query)
        startActivity(intent)
    }
}
