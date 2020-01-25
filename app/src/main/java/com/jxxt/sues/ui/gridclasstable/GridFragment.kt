package com.jxxt.sues.ui.gridclasstable

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.fragment.app.Fragment
import com.jxxt.sues.R
import kotlinx.android.synthetic.main.grid_class_table.*
import org.jetbrains.anko.backgroundColor
import java.io.File

class GridFragment : Fragment() {
    //read and judge
    private lateinit var file: File
    private lateinit var colorString: File
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val myContext = context!!
        file = File(myContext.filesDir, "/a")
        colorString = File(myContext.filesDir, "/color")
        return inflater.inflate(R.layout.grid_class_table, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (colorString.exists()) {
            val primeColor: Int = colorString.readText().toInt()
            val dark = ColorUtils.calculateLuminance(primeColor) <= 0.2
            if (dark) {
                grid_frag.backgroundColor = Color.BLACK
            } else {
                grid_frag.backgroundColor = Color.WHITE
            }
        }
    }
}