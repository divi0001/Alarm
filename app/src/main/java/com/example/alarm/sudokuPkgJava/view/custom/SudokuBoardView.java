package com.example.alarm.sudokuPkgJava.view.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.alarm.sudokuPkgJava.game.Cell;

public final class SudokuBoardView extends View {

    //hugely inspired/copied from Patrick Feltes on Youtube https://www.youtube.com/watch?v=00QdlHuKGH8
    private final Paint thickLinePaint;
    public AttributeSet attr;
    private final Paint thinLinePaint;
    private final Paint selectedCellPaint;
    private final Paint conflictingCellPaint;

    private int selectedRow = -1;
    private int selectedCol = -1;
    private float cHeight=-1;
    private float cWidth=-1;
    private OnTouchListener listener;
    private Cell[][] cells;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        updateMeasurements(canvas.getWidth(), canvas.getHeight());

        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                canvas.drawRect((i*(float)this.getWidth())/3, (j*(float)this.getHeight())/3, ((i+1)*(float)this.getWidth())/3, ((j+1)*(float)this.getHeight())/3,this.thickLinePaint);
                canvas.drawRect((i*(float)this.getWidth())/9, (j*(float)this.getHeight())/9, ((i+1)*(float)this.getWidth())/9, ((j+1)*(float)this.getHeight())/9,this.thinLinePaint);
                canvas.drawRect(((i+3)*(float)this.getWidth())/9, ((j+3)*(float)this.getHeight())/9, ((i+4)*(float)this.getWidth())/9, ((j+4)*(float)this.getHeight())/9,this.thinLinePaint);
                canvas.drawRect(((i+6)*(float)this.getWidth())/9, ((j+6)*(float)this.getHeight())/9, ((i+7)*(float)this.getWidth())/9, ((j+7)*(float)this.getHeight())/9,this.thinLinePaint);
                canvas.drawRect(((i+6)*(float)this.getWidth())/9, ((j+6)*(float)this.getHeight())/9, ((i+4)*(float)this.getWidth())/9, ((j+4)*(float)this.getHeight())/9,this.thinLinePaint);
                canvas.drawRect(((i+6)*(float)this.getWidth())/9, ((j+6)*(float)this.getHeight())/9, ((i+1)*(float)this.getWidth())/9, ((j+1)*(float)this.getHeight())/9,this.thinLinePaint);
                canvas.drawRect(((i+3)*(float)this.getWidth())/9, ((j+3)*(float)this.getHeight())/9, ((i+1)*(float)this.getWidth())/9, ((j+1)*(float)this.getHeight())/9,this.thinLinePaint);
                canvas.drawRect(((i+3)*(float)this.getWidth())/9, ((j+3)*(float)this.getHeight())/9, ((i+7)*(float)this.getWidth())/9, ((j+7)*(float)this.getHeight())/9,this.thinLinePaint);
                canvas.drawRect((i*(float)this.getWidth())/9, (j*(float)this.getHeight())/9, ((i+4)*(float)this.getWidth())/9, ((j+4)*(float)this.getHeight())/9,this.thinLinePaint);
                canvas.drawRect((i*(float)this.getWidth())/9, (j*(float)this.getHeight())/9, ((i+7)*(float)this.getWidth())/9, ((j+7)*(float)this.getHeight())/9,this.thinLinePaint);

            }
        }

        canvas.drawRect(0.0F, 0.0F, (float)this.getWidth(), (float)this.getHeight(), this.thickLinePaint);
        fillCells(canvas);
        this.cHeight = canvas.getHeight();
        this.cWidth = canvas.getWidth();
        drawText(canvas);
    }


    private void updateMeasurements(float width, float height){
        noteTextPaint().setTextSize(width/(9*3F));
        textPaint().setTextSize(width/(9*1.5F));
        startingCellTextPaint().setTextSize(width/(9*1.5F));
    }

    private void drawText(Canvas canvas) {
        if(canvas == null) {
            return;
        }
        for (Cell[] ce : cells) {
            for (Cell c : ce) {

                Rect textBounds = new Rect();
                int value = c.value;
                Paint noteTextPaint = noteTextPaint();

                if (value == 0) {
                    //draw notes
                    c.notes.forEach(note -> {
                        int rowInCell = (note - 1) / 3;
                        int colInCell = (note - 1) % 3;
                        String valueString = note.toString();
                        noteTextPaint.getTextBounds(valueString, 0, valueString.length(), textBounds);
                        float textWidth = noteTextPaint.measureText(valueString);
                        float textHeight = textBounds.height();

                        canvas.drawText(valueString,
                                (c.col * (canvas.getWidth() / 9F)) + (canvas.getWidth() / 27F) * colInCell + ((canvas.getWidth() / 27F) / 2) - textWidth / 2F,
                                (c.row * (canvas.getHeight() / 9F)) + (canvas.getHeight() / 27F) * rowInCell + ((canvas.getHeight() / 27F) / 2) + textHeight / 2F,
                                noteTextPaint);

                    });
                } else {
                    int row = c.row;
                    int col = c.col;

                    String valueString = String.valueOf(c.value);
                    Paint paintToUse = c.isStarting ? startingCellTextPaint() : textPaint();
                    paintToUse.getTextBounds(valueString, 0, valueString.length(), textBounds);
                    float textWidth = paintToUse.measureText(valueString);
                    float textHeight = textBounds.height();
                    canvas.drawText(valueString,
                            (col * (canvas.getWidth() / 3F)) + (canvas.getWidth() / 3F) / 2F - textWidth / 2F, //this used to be canvas.get...()/9F, trying 3F to see if it gets more readable that way
                            (row * (canvas.getHeight() / 3F)) + (canvas.getHeight() / 3F) / 2F + textHeight / 2F,
                            paintToUse);
                }


            }
        }
    }

    private Paint textPaint(){
        Paint pain = new Paint();
        pain.setStyle(Style.FILL_AND_STROKE);
        pain.setColor(Color.BLACK);
        return pain;
    }

    private Paint startingCellTextPaint(){
        Paint pain1 = new Paint();
        pain1.setStyle(Style.FILL_AND_STROKE);
        pain1.setColor(Color.BLACK);
        pain1.setTypeface(Typeface.DEFAULT_BOLD);
        return pain1;
    }

    private Paint startingCellPaint(){
        Paint pain2 = new Paint();
        pain2.setStyle(Style.FILL_AND_STROKE);
        pain2.setColor(Color.argb(100,132,132,132));
        return pain2;
    }

    private Paint noteTextPaint(){
        Paint pain3 = new Paint();
        pain3.setStyle(Style.FILL_AND_STROKE);
        pain3.setColor(Color.BLACK);

        return pain3;
    }



    private void fillCells(Canvas canvas) {

        if(selectedRow == -1 || selectedCol == -1) return;

        for (Cell[] ce: cells){
            for (Cell c: ce) {
                int i = c.row;
                int j = c.col;
                if(c.isStarting){
                    fillCell(canvas,i,j,startingCellPaint());
                }else if(i == selectedRow && j == selectedCol){
                    fillCell(canvas,i,j,selectedCellPaint);
                } else if (i == selectedRow || j == selectedCol) {
                    fillCell(canvas,i,j,conflictingCellPaint);
                } else if (i/3 == selectedRow/3 && j/3 == selectedCol/3) {
                    //Log.d("mett", "row: "+ i+" col: " + j + " are the box indices" );
                    fillCell(canvas,i,j,conflictingCellPaint);
                }
            }
        }

    }

    private void fillCell(Canvas canvas, int i, int j, Paint paint) {
        canvas.drawRect((float)(j*canvas.getWidth())/9F, (float)(i*canvas.getHeight())/9F, (float)((j+1)*canvas.getWidth())/9F, (float)((i+1)*canvas.getHeight())/9F,paint);
    }





    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int sizePx = Math.min(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(sizePx, sizePx);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            handleTouchEvent(event.getX(), event.getY());
            return true;
        }
        return false;
    }

    private void handleTouchEvent(float x, float y) {
        int possibleSelRow = (int)(y/(this.cHeight/9));
        int possibleSelCol = (int)(x/(this.cWidth/9));
        listener.onCellTouched(possibleSelRow, possibleSelCol);
    }

    public SudokuBoardView(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        this.attr = attributeSet;
        Paint var3 = new Paint();
        var3.setStyle(Style.STROKE);
        var3.setColor(-16777216);
        var3.setStrokeWidth(8.0F);
        this.thickLinePaint = var3;

        Paint p = new Paint();
        p.setStyle(Style.STROKE);
        p.setColor(-16777216);
        p.setStrokeWidth(4.0F);
        this.thinLinePaint = p;

        Paint p1 = new Paint();
        p1.setStyle(Style.FILL_AND_STROKE);
        p1.setColor(Color.argb(100,110,173,58));
        this.selectedCellPaint = p1;

        Paint p2 = new Paint();
        p2.setStyle(Style.FILL_AND_STROKE);
        p2.setColor(Color.argb(150, 165,181,187));
        this.conflictingCellPaint = p2;

    }

    public void updateSelectedCellUI(int row, int col){
        selectedRow = row;
        selectedCol = col;
        invalidate();
    }

    public void registerListener(OnTouchListener listener){
        this.listener = listener;
    }

    public void updateCells(Cell[][] cells) {
        this.cells = cells;
        invalidate();
    }


    public interface OnTouchListener {
        void onCellTouched(int row, int col);
    }


}