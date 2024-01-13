package com.example.mldetectliveobjects.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View

class Draw(context: Context?,val rect: Rect,val text:String):View(context) {

    private lateinit var boundaryPaint: Paint
    private lateinit var textPaint:Paint

    init {
        init()
    }

    private fun init() {
        boundaryPaint=Paint()
        boundaryPaint.color=Color.WHITE
        boundaryPaint.strokeWidth=20f
        boundaryPaint.style=Paint.Style.STROKE

        textPaint=Paint()
        textPaint.color=Color.RED
        textPaint.textSize=80f
        boundaryPaint.style=Paint.Style.FILL

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        textPaint.textAlign = Paint.Align.CENTER;
        canvas.drawRect(rect.left.toFloat(),rect.top.toFloat(),rect.right.toFloat(),rect.bottom.toFloat(),boundaryPaint)
        canvas.drawText(text,rect.centerX().toFloat(),rect.centerY().toFloat(),textPaint)

    }

}