package com.ysp.androidview.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.OverScroller;

import com.ysp.androidview.R;
import com.ysp.androidview.utils.DisplayUtil;
import com.ysp.androidview.utils.ItemCreator;

import java.util.List;

/**
 * 刻度尺
 */
public class RulerView extends View {
private enum  Direction {
    NONE, VERTICAL
}
private String label= "ml";
private List<Integer> items= ItemCreator.range(0, 40);
//最小值
private int mMinValue=200;
//最大值
private int mMaxValue=1000;
//几个格设置一个值
private int mRate=5;
//倍数
private int mMultiple=100;
//游标颜色
private int cursorColor = 0;
//刻度颜色
private int scaleColor = 0;
//整5刻度颜色
private int scalePointerColor = 0;
//可滚动高度
private float scrollHeight = 0f;
//刻度宽度
private int scaleWidth = 0;
//整5刻度宽度
private int scalePointerWidth = 0;
//游标宽度
private int cursorWidth = 0;
//刻度高度+刻度间距
private int scaleHeight = 0;
//刻度高度
private float scaleStrokeWidth = 0f;
//刻度画笔
private Paint scalePaint;
//整5刻度画笔
private Paint scalePointerPaint;
//整5刻度文字画笔
private Paint scalePointerTextPaint;
//游标画笔
private Paint cursorPaint;
//游标文字画笔
private Paint cursorTextPaint;
//标签文字画笔
private Paint cursorLabelPaint;
//刻度间距
private int offsetHeight = 0;
//刻度与文字的间距
private int cursorTextOffsetLeft = 0;
//刻度距离View左边的距离
private int scaleLeft = 0;
//整5刻度距离View左边的距离
private int pointerScaleLeft = 0;
//滚动控制器
private OverScroller scroller;
private int maxFlingVelocity = 0;
private int minFlingVelocity = 0;
private int touchSlop = 0;
//当前滚动方向
private Direction mCurrentScrollDirection = Direction.NONE;
//当前惯性滚动方向
private Direction mCurrentFlingDirection = Direction.NONE;
//当前滚动x,y
private PointF mCurrentOrigin = new PointF(0f, 0f);
//手势支持
private GestureDetectorCompat mGestureDetector;

   public RulerView(Context context){
     super(context);
     resolveAttribute(context, null, 0, 0);
     init(context);
   }

   public RulerView(Context context, AttributeSet attrs){
    super(context, attrs) ;
    resolveAttribute(context, attrs, 0, 0);
    init(context);
   }

  public RulerView(Context context, AttributeSet attrs,int defStyleAttr){
    super(context, attrs, defStyleAttr);
    resolveAttribute(context, attrs, defStyleAttr, 0);
    init(context);
  }

    /**
     * 从xml属性初始化参数
     */
   private void resolveAttribute(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
    scaleStrokeWidth = DisplayUtil.dpToPx(context, 1f) / 2f;
    scaleWidth = 50;
    scalePointerWidth =(int)(scaleWidth * 1.5);
    cursorWidth = (int)(scaleWidth * 3.333);
    scaleHeight = 5;
    cursorColor = context.getResources().getColor(R.color.cursor);
    scaleColor = context.getResources().getColor(R.color.scale);
    scalePointerColor = context.getResources().getColor(R.color.scale_pointer);
    TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ruler, defStyleAttr, defStyleRes);
    for (int i=0;i<a.getIndexCount();i++) {
        int attr = a.getIndex(i);
        if (attr == R.styleable.ruler_scaleWidth) {
        scaleWidth = a.getDimensionPixelOffset(attr, 50);
        scalePointerWidth = (int)(scaleWidth * 1.5);
        cursorWidth = (int)(scaleWidth * 3.333);
        } else if (attr == R.styleable.ruler_scaleHeight) {
           scaleHeight = a.getDimensionPixelOffset(attr, 5);
        } else if (attr == R.styleable.ruler_cursorColor) {
           cursorColor = a.getColor(attr, context.getResources().getColor(R.color.cursor));
        }else if (attr == R.styleable.ruler_scaleColor) {
            scaleColor = a.getColor(attr, context.getResources().getColor(R.color.scale));
        } else if (attr == R.styleable.ruler_scalePointerColor) {
            scalePointerColor = a.getColor(attr, context.getResources().getColor(R.color.scale_pointer));
        }
    }
      cursorTextOffsetLeft = DisplayUtil.dpToPx(context, 32f);
    a.recycle();
   }

/**
 * 初始化画笔、滚动控制器和手势对象
 */
  private void init(Context context) {
        scroller = new OverScroller(context);
        mGestureDetector = new GestureDetectorCompat(context, onGestureListener);
        maxFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();
        minFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        scalePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePaint.setStrokeWidth(DisplayUtil.dpToPx(context,1));
        scalePaint.setColor(scaleColor);
        scalePaint.setStyle(Paint.Style.STROKE);

        scalePointerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePointerPaint.setStrokeWidth(DisplayUtil.dpToPx(context,1));
        scalePointerPaint.setColor(scalePointerColor);
        scalePointerPaint.setStyle(Paint.Style.STROKE) ;

        scalePointerTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        scalePointerTextPaint.setColor(scalePointerColor);
        scalePointerTextPaint.setStyle(Paint.Style.STROKE);
        scalePointerTextPaint.setTextSize(Float.valueOf(DisplayUtil.spToPx(context, 16f)));

        cursorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorPaint.setStrokeWidth(DisplayUtil.dpToPx(context,1));
        cursorPaint.setColor(cursorColor);
        cursorPaint.setStyle( Paint.Style.STROKE);

        cursorTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorTextPaint.setColor(cursorColor);
        cursorTextPaint.setStyle (Paint.Style.STROKE);
        cursorTextPaint.setTextSize(Float.valueOf(DisplayUtil.spToPx(context, 32f))) ;

        cursorLabelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cursorLabelPaint.setColor(cursorColor);
        cursorLabelPaint.setStyle(Paint.Style.STROKE);
        cursorLabelPaint.setTextSize(Float.valueOf(DisplayUtil.spToPx(context, 20f)));
    }

    /**
     * 设置起始值
     * @param minValue 最小值
     * @param maxValue  最大值
     */
    public void setItemValue(int minValue,int maxValue){
      this.mMinValue=minValue;
      this.mMaxValue=maxValue;
        post(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        });
    }
    /**
     * 设置item数据
     */
    public void setItems(List<Integer> items) {
        this.items = items;
        this.scrollHeight = Float.valueOf(getHeight() + (this.items.size() - 1) * scaleHeight);
        post(new Runnable() {
            @Override
            public void run() {
                mCurrentOrigin.x = 0f;
                mCurrentOrigin.y = 0f;
                invalidate();
            }
         });
   }

    /**
     * 获取item数据
     */
    public List<Integer> getItems(){
       return items;
    }

    /**
     * 设置标签文字
     */
    public void setLabel(String label) {
        this.label = label;
        //重新初始化刻度左距离
        initScaleLeft();
        //通知重新绘制
        invalidate();
    }
    /**
     * 触控事件交给mGestureDetector
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mGestureDetector.onTouchEvent(event);
        //如果手指离开屏幕，并且没有惯性滑动
        if (event.getAction() == MotionEvent.ACTION_UP && mCurrentFlingDirection == Direction.NONE) {
            if (mCurrentScrollDirection == Direction.VERTICAL) {
                //检查是否需要对齐刻度
                snapScroll();
            }
            mCurrentScrollDirection = Direction.NONE;
        }
        return result;
    }
    /**
     * 计算View如何滑动
     */
    @Override
    public void computeScroll() {
        super.computeScroll();
        if (scroller.isFinished()) {//滚动以及完成
            if (mCurrentFlingDirection != Direction.NONE) {
                // Snap to day after fling is finished.
                mCurrentFlingDirection = Direction.NONE;
                snapScroll();//检查是否需要对齐刻度，如果需要，则自动滚动，让游标与刻度对齐
            }
        } else {
            //如果当前不处于滚动状态，则再次检查是否需要对齐刻度
            if (mCurrentFlingDirection != Direction.NONE && forceFinishScroll()) {
                snapScroll();
            } else if (scroller.computeScrollOffset()) {//检查是否滚动完成，并且计算新的滚动坐标
                mCurrentOrigin.y = Float.valueOf(scroller.getStartY());//记录当前y坐标
                checkOriginY();//检查坐标是否越界
                ViewCompat.postInvalidateOnAnimation(this);//通知重新绘制
            } else {//不作滚动
                float startY;
                if (mCurrentOrigin.y > 0)
                    startY= 0f;
                else if (mCurrentOrigin.y < getHeight() - getMeasuredHeight())
                    startY=getMeasuredHeight() - scrollHeight;
                else{
                    startY=mCurrentOrigin.y;
                }
                scroller.startScroll(0, (int)startY, 0, 0, 0);
            }
        }
    }
    /**
     * 测量控件大小，并初始化一些必要的属性
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        offsetHeight = height / 2 - scaleHeight / 2;
        scrollHeight = height + (items.size() - 1f) * scaleHeight;
        initScaleLeft();
        pointerScaleLeft = scaleLeft + scaleWidth - scalePointerWidth;
    }

    /**
     * 初始化刻度左间距
     */
    private void initScaleLeft() {
        Float[] labelSize = DisplayUtil.measureTextSize(cursorLabelPaint, label);
        scaleLeft = (int) ((getMeasuredWidth() - scalePointerWidth + cursorTextOffsetLeft + labelSize[0]) / 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (items.isEmpty())
            return;
        drawScale(canvas);//绘制刻度
        drawCursor(canvas); //绘制游标
    }

    /**
     * 画刻度
     * @param canvas
     */
   private void  drawScale(Canvas canvas) {
        for (int i=0;i< items.size();i++) {//根据给定的item信息绘制刻度
            float top = offsetHeight + mCurrentOrigin.y + i * scaleHeight + scaleHeight / 2;
            if (0 == i % 5) {//绘制整5刻度
                canvas.drawRect(new RectF(Float.valueOf(pointerScaleLeft), top - scaleStrokeWidth,
                                Float.valueOf(pointerScaleLeft) + scalePointerWidth, top + scaleStrokeWidth), scalePointerPaint);
                if (Math.abs(getSelectedItem() - i) > 1) {//整5刻度有文字，所以需要计算文字位置，并绘制文字
                    String text = String.valueOf(getTextValue(i));
                    Float[] size = DisplayUtil.measureTextSize(scalePointerTextPaint, text);
                    canvas.drawText(text, pointerScaleLeft - size[0] * 1.3f, top + size[1] / 2, scalePointerTextPaint);
                  }
            } else {//绘制普通刻度
                canvas.drawRect(new RectF(Float.valueOf(scaleLeft), top - scaleStrokeWidth,
                Float.valueOf(scaleLeft) + scaleWidth, top + scaleStrokeWidth), scalePaint);
            }
        }
   }

    /**
     * 绘制游标，这里也需要计算文字位置，包括item文字和标签文字
     */
    private void drawCursor(Canvas canvas) {
        int left = scaleLeft + scaleWidth - cursorWidth;
        int top = (int) (getMeasuredHeight() / 2f);
        canvas.drawRect(new RectF(Float.valueOf(left), Float.valueOf(top) - scaleStrokeWidth,
                        Float.valueOf(left) + cursorWidth, Float.valueOf(top) + scaleStrokeWidth),
        cursorPaint);
        String text = String.valueOf(getTextValue(getSelectedItem()));
        Float[] textSize =DisplayUtil.measureTextSize(cursorTextPaint, text);
        Float[] labelSize = DisplayUtil.measureTextSize(cursorLabelPaint, label);
        float labelLeft = left - cursorTextOffsetLeft - labelSize[0];
        float textOffset = (textSize[0] - labelSize[0]) / 2f;
        canvas.drawText(text, left - cursorTextOffsetLeft - textSize[0] + textOffset, top + textSize[1] / 2, cursorTextPaint);
        canvas.drawText(label, labelLeft, top + textSize[1] + labelSize[1], cursorLabelPaint);
     }

       private Boolean forceFinishScroll(){
        return scroller.getCurrVelocity()<= minFlingVelocity;
      }

    /**
     * 与刻度对齐
     */
    private void snapScroll() {
        scroller.computeScrollOffset();
        int nearestOrigin = -getSelectedItem() * scaleHeight;
        mCurrentOrigin.y = Float.valueOf(nearestOrigin);
        ViewCompat.postInvalidateOnAnimation(RulerView.this);
    }

    /**
     * 检查y坐标越界
     */
    private void checkOriginY() {
        if (mCurrentOrigin.y > 0)
            mCurrentOrigin.y = 0f;
        if (mCurrentOrigin.y < getMeasuredHeight() - scrollHeight)
            mCurrentOrigin.y = getMeasuredHeight() - scrollHeight;
    }
    private int getTextValue(int index){
        int value;
        if (index >= items.size()){
            value=mMaxValue;
        }else if (index <= 0){
            value = mMinValue;
        }else{
            int i=index/mRate;
            value=mMinValue+(i*mMultiple);
        }
        return value;
    }
    /**
     * 获取选中的值
     * @return
     */
    public int getSelectedValue(){
        int value;
        int index = -Math.round(mCurrentOrigin.y / scaleHeight);
        if (index >= items.size()){
            value=mMaxValue;
        }else if (index <= 0){
            value = mMinValue;
        }else{
            int i=index/mRate;
            value=mMinValue+(i*mMultiple);
        }
        return value;
    }
    /**
     * 获取选中的item
     */
    public int getSelectedItem(){
        int index = -Math.round(mCurrentOrigin.y / scaleHeight);
        if (index >= items.size()) index = items.size() - 1;
        if (index < 0)
            index = 0;
        return index;
    }

    /**
     * 设置选中的值
     * @param value
     */
    public void setSelectedValue(final int value) {
        post(new Runnable() {
            @Override
            public void run() {
                int index=(value-mMinValue)/mMultiple*mRate;
                mCurrentOrigin.y = -(scaleHeight * index);
                checkOriginY();
                ViewCompat.postInvalidateOnAnimation(RulerView.this);
                snapScroll();
            }
        });
    }
        /**
         * 设置选中item
         */
     public void setSelectedItem(final int index) {
        post(new Runnable() {
            @Override
            public void run() {
                mCurrentOrigin.y = -(scaleHeight * index);
                checkOriginY();
                ViewCompat.postInvalidateOnAnimation(RulerView.this);
                snapScroll();
            }
        });
    }

/**
 * 手势监听
 */
  private GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {
    /**
     * 手指拖动回调
     */
    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //如果当前正在滚动，则停止滚动
        scroller.forceFinished(true);
        if (Direction.NONE == mCurrentScrollDirection) {//判断滚动方向，这里只有垂直一个方向
          if (Math.abs(distanceX) < Math.abs(distanceY)) {
              mCurrentScrollDirection= Direction.VERTICAL;
            } else {
              mCurrentScrollDirection= Direction.NONE;
            }
        }
        // Calculate the new origin after scroll.
        if (mCurrentScrollDirection== Direction.VERTICAL) {
                mCurrentOrigin.y -= distanceY;
                checkOriginY();
                ViewCompat.postInvalidateOnAnimation(RulerView.this);
        }
        return true;
    }
    /**
     * 手指按下回调，这里将状态标记为非滚动状态
     */
    @Override
    public boolean onDown(MotionEvent e) {
        getParent().requestDisallowInterceptTouchEvent(true);
        mCurrentScrollDirection = Direction.NONE;
        return true;
    }
    /**
     * 惯性滚动回调
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        scroller.forceFinished(true);
        mCurrentFlingDirection = mCurrentScrollDirection;
        if (mCurrentFlingDirection== Direction.VERTICAL) {
            scroller.fling((int)mCurrentOrigin.x, (int)mCurrentOrigin.y,
                    0, (int)velocityY, Integer.MIN_VALUE,
                    Integer.MAX_VALUE, Integer.MIN_VALUE, 0);
        }
        ViewCompat.postInvalidateOnAnimation(RulerView.this);
        return true;
    }
  };
 }
