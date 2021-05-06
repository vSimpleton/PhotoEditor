package com.vsimpleton.photoeditor

import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsimpleton.photoeditor.databinding.ActivityEditBinding

class EditActivity : BaseActivity<ActivityEditBinding>() {

    private val mEditAdapter: EditAdapter by lazy { EditAdapter() }
    private val mFontAdapter: FontAdapter by lazy { FontAdapter() }
    private val mBackgroundColorAdapter: ColorAdapter by lazy { ColorAdapter() }
    private val mFontColorAdapter: ColorAdapter by lazy { ColorAdapter() }
    private var currentId = 0

    private val uri: Uri by lazy {
        (intent?.extras?.getParcelable(EXTRA_URI) as Uri?) ?: Uri.parse("")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initListener()
        initRecyclerView()
    }

    private fun initView() {
        mBinding.mixtureView.setImageUri(uri)
    }

    private fun initRecyclerView() {
        val layoutManager = GridLayoutManager(this, 5)
        mBinding.rcyContent.layoutManager = layoutManager
        mBinding.rcyContent.adapter = mEditAdapter

        val fontLayoutManager = LinearLayoutManager(this)
        fontLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.rcyFont.layoutManager = fontLayoutManager
        mBinding.rcyFont.adapter = mFontAdapter

        val colorLayoutManager = LinearLayoutManager(this)
        colorLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.rcyBackgroundColor.layoutManager = colorLayoutManager
        mBinding.rcyBackgroundColor.adapter = mBackgroundColorAdapter

        val fontColorLayoutManager = LinearLayoutManager(this)
        fontColorLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.rcyFontColor.layoutManager = fontColorLayoutManager
        mBinding.rcyFontColor.adapter = mFontColorAdapter

        initAdapterListener()
    }

    private fun initAdapterListener() {
        mEditAdapter.onItemClickListener = {

        }

        mFontAdapter.onItemClickListener = {

        }

        mBackgroundColorAdapter.onItemClickListener = {

        }

        mFontColorAdapter.onItemClickListener = {

        }
    }

    private fun initListener() {
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.ivSave.setOnClickListener {

        }

        mBinding.rg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbText -> {
                    mBinding.rcyContent.visibility = View.GONE
                    mBinding.clTextLayout.visibility = View.VISIBLE
                    mFontAdapter.setData(DataHelper.getFontLists(this))
                    mBackgroundColorAdapter.setData(DataHelper.getColorLists())
                    mFontColorAdapter.setData(DataHelper.getColorLists())
                }
                R.id.rbEmoji -> {
                    mBinding.rcyContent.visibility = View.VISIBLE
                    mBinding.clTextLayout.visibility = View.GONE
                    mEditAdapter.setData(DataHelper.getEmojiLists(this))
                }
                R.id.rbSticker -> {
                    mBinding.rcyContent.visibility = View.VISIBLE
                    mBinding.clTextLayout.visibility = View.GONE
                    mEditAdapter.setData(DataHelper.getStickerLists(this))
                }
                R.id.rbFilter -> {
                    mBinding.rcyContent.visibility = View.VISIBLE
                    mBinding.clTextLayout.visibility = View.GONE
                    mEditAdapter.setData(DataHelper.getFilterLists(this))
                }
            }
        }
    }

    override fun onBackPressed() {
        if (mBinding.rcyContent.visibility == View.VISIBLE || mBinding.clTextLayout.visibility == View.VISIBLE) {
            mBinding.rg.clearCheck()
            mBinding.rcyContent.visibility = View.GONE
            mBinding.clTextLayout.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }

}