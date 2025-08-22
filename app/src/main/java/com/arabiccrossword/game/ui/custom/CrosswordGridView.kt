package com.arabiccrossword.game.ui.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.arabiccrossword.game.model.CrosswordCell
import com.arabiccrossword.game.model.CrosswordPuzzle
import com.arabiccrossword.game.model.CellType
import com.arabiccrossword.game.R
import kotlin.math.min

class CrosswordGridView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    
    private var puzzle: CrosswordPuzzle? = null
    private var cellSize: Float = 60f
    private var gridPadding: Float = 20f
    private var selectedRow: Int = -1
    private var selectedCol: Int = -1
    
    // Paint objects
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val cellPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val selectionPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    
    // Callbacks
    private var onCellClickListener: ((Int, Int) -> Unit)? = null
    private var onTextChangedListener: ((Int, Int, String) -> Unit)? = null
    
    init {
        setupPaints()
    }
    
    private fun setupPaints() {
        // Grid lines
        gridPaint.apply {
            color = context.getColor(R.color.grid_border)
            strokeWidth = 2f
            style = Paint.Style.STROKE
        }
        
        // Cell background
        cellPaint.apply {
            color = context.getColor(R.color.white)
            style = Paint.Style.FILL
        }
        
        // Text
        textPaint.apply {
            color = context.getColor(R.color.text_primary)
            textSize = cellSize * 0.6f
            textAlign = Paint.Align.CENTER
            isFakeBoldText = true
            typeface = Typeface.DEFAULT
        }
        
        // Cell numbers
        numberPaint.apply {
            color = context.getColor(R.color.text_secondary)
            textSize = cellSize * 0.25f
            textAlign = Paint.Align.LEFT
            typeface = Typeface.DEFAULT
        }
        
        // Selection highlight
        selectionPaint.apply {
            color = context.getColor(R.color.grid_selected)
            style = Paint.Style.FILL
            alpha = 100
        }
    }
    
    fun setPuzzle(newPuzzle: CrosswordPuzzle) {
        puzzle = newPuzzle
        calculateOptimalCellSize()
        invalidate()
    }
    
    private fun calculateOptimalCellSize() {
        puzzle?.let { puzzle ->
            val (rows, cols) = puzzle.getGridSize()
            val availableWidth = width - 2 * gridPadding
            val availableHeight = height - 2 * gridPadding
            
            val cellSizeX = availableWidth / cols
            val cellSizeY = availableHeight / rows
            
            cellSize = min(cellSizeX, cellSizeY).coerceAtMost(80f).coerceAtLeast(40f)
            
            // Update text sizes
            textPaint.textSize = cellSize * 0.6f
            numberPaint.textSize = cellSize * 0.25f
        }
    }
    
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculateOptimalCellSize()
    }
    
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        
        puzzle?.let { puzzle ->
            drawGrid(canvas, puzzle)
        }
    }
    
    private fun drawGrid(canvas: Canvas, puzzle: CrosswordPuzzle) {
        val (rows, cols) = puzzle.getGridSize()
        
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val cell = puzzle.grid[row][col]
                drawCell(canvas, cell, row, col)
            }
        }
    }
    
    private fun drawCell(canvas: Canvas, cell: CrosswordCell, row: Int, col: Int) {
        val x = gridPadding + col * cellSize
        val y = gridPadding + row * cellSize
        
        // Draw cell background
        when (cell.type) {
            CellType.PLAYABLE -> {
                if (cell.isSelected) {
                    canvas.drawRect(x, y, x + cellSize, y + cellSize, selectionPaint)
                }
                canvas.drawRect(x, y, x + cellSize, y + cellSize, cellPaint)
            }
            CellType.FILLED -> {
                cellPaint.color = context.getColor(R.color.grid_filled)
                canvas.drawRect(x, y, x + cellSize, y + cellSize, cellPaint)
                cellPaint.color = context.getColor(R.color.white)
            }
            CellType.BLANK -> {
                // Don't draw blank cells
                return
            }
        }
        
        // Draw cell border
        canvas.drawRect(x, y, x + cellSize, y + cellSize, gridPaint)
        
        // Draw cell number
        cell.number?.let { number ->
            val numberX = x + 5f
            val numberY = y + numberPaint.textSize + 5f
            canvas.drawText(number.toString(), numberX, numberY, numberPaint)
        }
        
        // Draw cell content
        when (cell.type) {
            CellType.PLAYABLE -> {
                if (cell.value.isNotEmpty()) {
                    val textX = x + cellSize / 2
                    val textY = y + cellSize / 2 + textPaint.textSize / 3
                    
                    // Set text color based on completion status
                    textPaint.color = if (cell.isCompleted) {
                        context.getColor(R.color.text_success)
                    } else {
                        context.getColor(R.color.text_primary)
                    }
                    
                    canvas.drawText(cell.value, textX, textY, textPaint)
                }
            }
            CellType.FILLED -> {
                if (cell.value.isNotEmpty()) {
                    val textX = x + cellSize / 2
                    val textY = y + cellSize / 2 + textPaint.textSize / 3
                    textPaint.color = context.getColor(R.color.white)
                    canvas.drawText(cell.value, textX, textY, textPaint)
                }
            }
            else -> { /* Do nothing */ }
        }
    }
    
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val (row, col) = getCellFromTouch(event.x, event.y)
                if (row >= 0 && col >= 0) {
                    selectedRow = row
                    selectedCol = col
                    onCellClickListener?.invoke(row, col)
                    invalidate()
                    return true
                }
            }
        }
        return super.onTouchEvent(event)
    }
    
    private fun getCellFromTouch(touchX: Float, touchY: Float): Pair<Int, Int> {
        puzzle?.let { puzzle ->
            val (rows, cols) = puzzle.getGridSize()
            
            for (row in 0 until rows) {
                for (col in 0 until cols) {
                    val x = gridPadding + col * cellSize
                    val y = gridPadding + row * cellSize
                    
                    if (touchX >= x && touchX <= x + cellSize &&
                        touchY >= y && touchY <= y + cellSize) {
                        
                        val cell = puzzle.grid[row][col]
                        if (cell.type != CellType.BLANK) {
                            return Pair(row, col)
                        }
                    }
                }
            }
        }
        return Pair(-1, -1)
    }
    
    fun setOnCellClickListener(listener: (Int, Int) -> Unit) {
        onCellClickListener = listener
    }
    
    fun setOnTextChangedListener(listener: (Int, Int, String) -> Unit) {
        onTextChangedListener = listener
    }
    
    fun updateCellValue(row: Int, col: Int, value: String) {
        puzzle?.let { puzzle ->
            if (puzzle.isValidCell(row, col)) {
                puzzle.setCellValue(row, col, value)
                onTextChangedListener?.invoke(row, col, value)
                invalidate()
            }
        }
    }
    
    fun selectCell(row: Int, col: Int) {
        selectedRow = row
        selectedCol = col
        puzzle?.selectCell(row, col)
        invalidate()
    }
    
    fun clearSelection() {
        selectedRow = -1
        selectedCol = -1
        puzzle?.let { puzzle ->
            puzzle.getSelectedCell()?.let { (row, col) ->
                puzzle.grid[row][col].isSelected = false
            }
        }
        invalidate()
    }
}