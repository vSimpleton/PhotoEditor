package com.vsimpleton.photoeditor.view

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.SeekBar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.vsimpleton.photoeditor.*
import com.vsimpleton.photoeditor.adapter.ColorAdapter
import com.vsimpleton.photoeditor.adapter.EditAdapter
import com.vsimpleton.photoeditor.adapter.FontAdapter
import com.vsimpleton.photoeditor.databinding.ActivityEditBinding
import com.vsimpleton.photoeditor.utlis.BitmapManager
import com.vsimpleton.photoeditor.utlis.DataManager

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
        // Emoji、Sticker、Filter的layoutManager
        val layoutManager = GridLayoutManager(this, 5)
        mBinding.rcyContent.layoutManager = layoutManager
        mBinding.rcyContent.adapter = mEditAdapter

        // 字体的layoutManager
        val fontLayoutManager = LinearLayoutManager(this)
        fontLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.rcyFont.layoutManager = fontLayoutManager
        mBinding.rcyFont.adapter = mFontAdapter

        // 字体背景颜色的layoutManager
        val colorLayoutManager = LinearLayoutManager(this)
        colorLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.rcyBackgroundColor.layoutManager = colorLayoutManager
        mBinding.rcyBackgroundColor.adapter = mBackgroundColorAdapter

        // 字体颜色的layoutManager
        val fontColorLayoutManager = LinearLayoutManager(this)
        fontColorLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mBinding.rcyFontColor.layoutManager = fontColorLayoutManager
        mBinding.rcyFontColor.adapter = mFontColorAdapter

        initAdapterListener()
    }

    private fun initAdapterListener() {
        mEditAdapter.onItemClickListener = {
            when (currentId) {
                R.id.rbEmoji -> mBinding.mixtureView.addEmoji(DataManager.getEmojiLists(this)[it])
                R.id.rbSticker -> mBinding.mixtureView.addSticker(DataManager.getStickerLists(this)[it])
                R.id.rbFilter -> mBinding.mixtureView.setFilter(DataManager.getFilters()[it])
            }
            mBinding.clTextLayout.visibility = View.GONE
        }

        mFontAdapter.onItemClickListener = {
            mBinding.tvText.typeface =
                Typeface.createFromAsset(assets, DataManager.getFontLists(this)[it])
        }

        mBackgroundColorAdapter.onItemClickListener = {
            if (it == 0) {
                showColorPicker()
            } else {
                mBinding.tvText.setBackgroundColor(Color.parseColor(DataManager.getColorLists()[it]))
            }
        }

        mFontColorAdapter.onItemClickListener = {
            if (it == 0) {
                showColorPicker(false)
            } else {
                mBinding.tvText.setTextColor(Color.parseColor(DataManager.getColorLists()[it]))
            }
        }
    }

    private fun showColorPicker(background: Boolean = true) {
        ColorPickerDialogBuilder.with(this).setTitle("Choose Color").initialColor(Color.WHITE)
            .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
            .setPositiveButton("OK") { _, lastSelectedColor, _ ->
                if (background) {
                    mBinding.tvText.setBackgroundColor(lastSelectedColor)
                } else {
                    mBinding.tvText.setTextColor(lastSelectedColor)
                }
            }.setNegativeButton("Cancel", null).build().show()
    }

    private fun initListener() {
        mBinding.ivBack.setOnClickListener {
            onBackPressed()
        }

        mBinding.ivSave.setOnClickListener {
            if (mBinding.clTextLayout.visibility == View.VISIBLE) {
                mBinding.clTextLayout.visibility = View.GONE
                if (mBinding.tvText.text.isNotEmpty()) {
                    mBinding.mixtureView.addText(mBinding.tvText)
                }
                mBinding.tvText.text = ""
                mBinding.etText.text.clear()
            } else {
                BitmapManager.bitmap = mBinding.mixtureView.getBitmap()
                startActivity(Intent(this@EditActivity, ResultActivity::class.java))
            }
        }

        mBinding.etText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                mBinding.tvText.text = s
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        mBinding.sbFontSize.progress = mBinding.tvText.textSize.toInt()
        mBinding.sbFontSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinding.tvText.textSize = progress.toFloat()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        mBinding.rg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbText -> {
                    mBinding.rcyContent.visibility = View.GONE
                    mBinding.clTextLayout.visibility = View.VISIBLE
                    mFontAdapter.setData(DataManager.getFontLists(this))
                    mBackgroundColorAdapter.setData(DataManager.getColorLists())
                    mFontColorAdapter.setData(DataManager.getColorLists())
                }
                R.id.rbEmoji -> {
                    mBinding.rcyContent.visibility = View.VISIBLE
                    mBinding.clTextLayout.visibility = View.GONE
                    mEditAdapter.setData(DataManager.getEmojiLists(this))
                }
                R.id.rbSticker -> {
                    mBinding.rcyContent.visibility = View.VISIBLE
                    mBinding.clTextLayout.visibility = View.GONE
                    mEditAdapter.setData(DataManager.getStickerLists(this))
                }
                R.id.rbFilter -> {
                    mBinding.rcyContent.visibility = View.VISIBLE
                    mBinding.clTextLayout.visibility = View.GONE
                    mEditAdapter.setData(DataManager.getFilterLists(this))
                }
            }
            currentId = checkedId
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