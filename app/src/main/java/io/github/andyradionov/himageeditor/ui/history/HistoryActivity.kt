package io.github.andyradionov.himageeditor.ui.history

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import io.github.andyradionov.himageeditor.App
import io.github.andyradionov.himageeditor.R
import io.github.andyradionov.himageeditor.model.entity.Picture
import io.github.andyradionov.himageeditor.presentation.history.HistoryContract
import io.github.andyradionov.himageeditor.ui.common.ImagesAdapter
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity(), HistoryContract.View {

    private lateinit var presenter: HistoryContract.Presenter
    private lateinit var imagesAdapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        actionBar?.setDisplayHomeAsUpEnabled(true)

        initPresenter()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.detachView()
    }

    override fun showHistory(pictures: List<Picture>) {
        runOnUiThread {
            if (pictures.isEmpty()) {
                showViews(showEmpty = View.VISIBLE)
            } else {
                setupRecycler(pictures)
            }
        }
    }

    private fun initPresenter() {

        showViews(showLoading = View.VISIBLE)
        presenter = App.historyPresenter
        presenter.attachView(this)
    }

    private fun setupRecycler(pictures: List<Picture>) {
        imagesAdapter = ImagesAdapter(null, pictures)

        val layoutManager = LinearLayoutManager(this)
        recycler.adapter = imagesAdapter
        recycler.layoutManager = layoutManager
        showViews(showRecycler = View.VISIBLE)
    }

    private fun showViews(showRecycler: Int = View.INVISIBLE,
                          showEmpty: Int = View.INVISIBLE,
                          showLoading: Int = View.INVISIBLE) {
        recycler.visibility = showRecycler
        tvEmpty.visibility = showEmpty
        pbLoading.visibility = showLoading
    }
}
