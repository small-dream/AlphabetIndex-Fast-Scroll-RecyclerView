package in.myinnos.alphabetsindexfastscrollrecycler;

/**
 * Created by MyInnos on 31-01-2017.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

import androidx.annotation.ColorInt;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class IndexFastScrollRecyclerSection extends RecyclerView.AdapterDataObserver {

    private float mIndexbarWidth;
    private float mIndexbarMarginV;
    private float mIndexbarMarginH = 30;
    private float mPreviewPadding;
    private float mDensity;
    private float mScaledDensity;
    private int mListViewWidth;
    private int mListViewHeight;
    private int mCurrentSection = -1;
    private int mSelectSection = -1;
    private boolean mIsIndexing = false;
    private RecyclerView mRecyclerView = null;
    private Indexer mIndexer = null;
    private String[] mSections = null;
    private RectF mIndexbarRect;

    private int setIndexTextSize;
    private float setIndexbarWidth;
    private float setIndexbarMargin;
    private int setPreviewPadding;
    private boolean previewVisibility = true;
    private int setIndexBarCornerRadius;
    private Typeface setTypeface = null;
    private Boolean setIndexBarVisibility = true;
    private Boolean setSetIndexBarHighLateTextVisibility = false;
    private @ColorInt
    int indexbarBackgroudColor;
    private @ColorInt
    int indexbarTextColor;
    private @ColorInt
    int indexbarHighLateTextColor;

    private int setPreviewTextSize;
    private @ColorInt
    int previewBackgroundColor;
    private @ColorInt
    int previewTextColor;
    private int previewBackgroudAlpha;
    private int indexbarBackgroudAlpha;
    @ColorInt
    private int mBgCircleColor;

    Paint indexbarPaint;
    Paint previewPaint;
    Paint previewTextPaint;
    Paint indexPaint;
    private Paint mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public IndexFastScrollRecyclerSection(Context context, IndexFastScrollRecyclerView rv) {

        setIndexTextSize = rv.setIndexTextSize;
        setIndexbarWidth = rv.mIndexbarWidth;
        setIndexbarMargin = rv.mIndexbarMargin;
        setPreviewPadding = rv.mPreviewPadding;
        setPreviewTextSize = rv.mPreviewTextSize;
        previewBackgroundColor = rv.mPreviewBackgroudColor;
        previewTextColor = rv.mPreviewTextColor;
        mBgCircleColor = rv.mBgCircleColor;
        previewBackgroudAlpha = convertTransparentValueToBackgroundAlpha(rv.mPreviewTransparentValue);

        setIndexBarCornerRadius = rv.mIndexBarCornerRadius;
        indexbarBackgroudColor = rv.mIndexbarBackgroudColor;
        indexbarTextColor = rv.mIndexbarTextColor;
        indexbarHighLateTextColor = rv.mIndexbarHighLateTextColor;

        indexbarBackgroudAlpha = convertTransparentValueToBackgroundAlpha(rv.mIndexBarTransparentValue);

        mDensity = context.getResources().getDisplayMetrics().density;
        mScaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        mRecyclerView = rv;
        setAdapter(mRecyclerView.getAdapter());

        mIndexbarWidth = setIndexbarWidth * mDensity;
        mIndexbarMarginV = setIndexbarMargin * mDensity;
        mPreviewPadding = setPreviewPadding * mDensity;

        initPaint();
    }

    private void initPaint() {
        indexbarPaint = new Paint();
        indexbarPaint.setColor(indexbarBackgroudColor);
        indexbarPaint.setAlpha(indexbarBackgroudAlpha);
        indexbarPaint.setAntiAlias(true);

        previewPaint = new Paint();
        previewPaint.setColor(previewBackgroundColor);
        previewPaint.setAlpha(previewBackgroudAlpha);
        previewPaint.setAntiAlias(true);
        previewPaint.setShadowLayer(3, 0, 0, Color.argb(64, 0, 0, 0));

        previewTextPaint = new Paint();
        previewTextPaint.setColor(previewTextColor);
        previewTextPaint.setAntiAlias(true);
        previewTextPaint.setTextSize(setPreviewTextSize * mScaledDensity);
        previewTextPaint.setTypeface(setTypeface);

        indexPaint = new Paint();
        indexPaint.setColor(indexbarTextColor);
        indexPaint.setAntiAlias(true);
        indexPaint.setTextSize(setIndexTextSize * mScaledDensity);
        indexPaint.setTypeface(setTypeface);
    }

    public void draw(Canvas canvas) {

        if (setIndexBarVisibility) {
            canvas.drawRoundRect(mIndexbarRect, setIndexBarCornerRadius * mDensity, setIndexBarCornerRadius * mDensity, indexbarPaint);
            if (mSections != null && mSections.length > 0) {
                // Preview is shown when mCurrentSection is set
                if (previewVisibility && mCurrentSection >= 0 && !mSections[mCurrentSection].equals("")) {
                    float previewTextWidth = previewTextPaint.measureText(mSections[mCurrentSection]);
                    float previewSize = 2 * mPreviewPadding + previewTextPaint.descent() - previewTextPaint.ascent();
                    previewSize = Math.max(previewSize, previewTextWidth + 2 * mPreviewPadding);
                    RectF previewRect = new RectF((mListViewWidth - previewSize) / 2
                            , (mListViewHeight - previewSize) / 2
                            , (mListViewWidth - previewSize) / 2 + previewSize
                            , (mListViewHeight - previewSize) / 2 + previewSize);

                    canvas.drawRoundRect(previewRect, 5 * mDensity, 5 * mDensity, previewPaint);
                    canvas.drawText(mSections[mCurrentSection], previewRect.left + (previewSize - previewTextWidth) / 2 - 1
                            , previewRect.top + (previewSize - (previewTextPaint.descent() - previewTextPaint.ascent())) / 2 - previewTextPaint.ascent(), previewTextPaint);
                    fade(300);
                }

                float sectionHeight = (mIndexbarRect.height() - 2 * mIndexbarMarginV) / mSections.length;
                float paddingTop = (sectionHeight - (indexPaint.descent() - indexPaint.ascent())) / 2;
                for (int i = 0; i < mSections.length; i++) {
                    if (setSetIndexBarHighLateTextVisibility) {
                        float textW = indexPaint.measureText(mSections[i]);
                        Rect bounds = new Rect();
                        indexPaint.getTextBounds(mSections[i], 0, mSections[i].length(), bounds);
                        int height = bounds.height();
                        float paddingLeft = (mIndexbarWidth - textW) / 2;
                        if (mSelectSection > -1 && i == mSelectSection) {
                            mBgPaint.setColor(mBgCircleColor);
                            indexPaint.setTypeface(setTypeface);
                            indexPaint.setTextSize((setIndexTextSize) * mScaledDensity);
                            indexPaint.setColor(indexbarHighLateTextColor);
                            canvas.drawCircle(mIndexbarRect.left + paddingLeft + textW / 2, mIndexbarRect.top + mIndexbarMarginV + sectionHeight * i + paddingTop - indexPaint.ascent() - height / 2f, 30, mBgPaint);
                        } else {
                            indexPaint.setTypeface(setTypeface);
                            indexPaint.setTextSize(setIndexTextSize * mScaledDensity);
                            indexPaint.setColor(indexbarTextColor);
                        }
                        canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft
                                , mIndexbarRect.top + mIndexbarMarginV + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);


                    } else {
                        float paddingLeft = (mIndexbarWidth - indexPaint.measureText(mSections[i])) / 2;
                        canvas.drawText(mSections[i], mIndexbarRect.left + paddingLeft
                                , mIndexbarRect.top + mIndexbarMarginV + sectionHeight * i + paddingTop - indexPaint.ascent(), indexPaint);
                    }

                }
            }
        }

    }

    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // If down event occurs inside index bar region, start indexing
                if (contains(ev.getX(), ev.getY())) {

                    // It demonstrates that the motion event started from index bar
                    mIsIndexing = true;
                    // Determine which section the point is in, and move the list to that section
                    mCurrentSection = getSectionByPoint(ev.getY());
                    mSelectSection = mCurrentSection;
                    scrollToPosition();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mIsIndexing) {
                    // If this event moves inside index bar
                    if (contains(ev.getX(), ev.getY())) {
                        // Determine which section the point is in, and move the list to that section
                        mCurrentSection = getSectionByPoint(ev.getY());
                        mSelectSection = mCurrentSection;
                        scrollToPosition();
                    }
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mIsIndexing) {
                    mIsIndexing = false;
                    mCurrentSection = -1;
                }
                break;
        }
        return false;
    }

    private void scrollToPosition() {
        try {
            int position = mIndexer.getPositionForSection(mCurrentSection);
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                ((LinearLayoutManager) layoutManager).scrollToPositionWithOffset(position, 0);
            } else {
                layoutManager.scrollToPosition(position);
            }
        } catch (Exception e) {
            Log.d("INDEX_BAR", "Data size returns null");
        }
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        mListViewWidth = w;
        mListViewHeight = h;
        mIndexbarRect = new RectF(w - mIndexbarMarginH - mIndexbarWidth
                , mIndexbarMarginV
                , w - mIndexbarMarginH
                , h - mIndexbarMarginV);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter instanceof Indexer) {
            adapter.registerAdapterDataObserver(this);
            mIndexer = (Indexer) adapter;
            mSections = (String[]) mIndexer.getSections();
        }
    }

    public ArrayList<Integer> getIndex() {
        return mIndexer.getIndex();
    }

    @Override
    public void onChanged() {
        super.onChanged();
        updateSections();
    }

    public void updateSections() {
        mSections = (String[]) mIndexer.getSections();
    }

    public boolean contains(float x, float y) {
        // Determine if the point is in index bar region, which includes the right margin of the bar
        return (x >= mIndexbarRect.left && y >= mIndexbarRect.top && y <= mIndexbarRect.top + mIndexbarRect.height());
    }

    private int getSectionByPoint(float y) {
        if (mSections == null || mSections.length == 0)
            return 0;
        if (y < mIndexbarRect.top + mIndexbarMarginV)
            return 0;
        if (y >= mIndexbarRect.top + mIndexbarRect.height() - mIndexbarMarginV)
            return mSections.length - 1;
        return (int) ((y - mIndexbarRect.top - mIndexbarMarginV) / ((mIndexbarRect.height() - 2 * mIndexbarMarginV) / mSections.length));
    }

    private Runnable mLastFadeRunnable = null;

    private void fade(long delay) {
        if (mRecyclerView != null) {
            if (mLastFadeRunnable != null) {
                mRecyclerView.removeCallbacks(mLastFadeRunnable);
            }
            mLastFadeRunnable = new Runnable() {
                @Override
                public void run() {
                    mRecyclerView.invalidate();
                }
            };
            mRecyclerView.postDelayed(mLastFadeRunnable, delay);
        }
    }

    private int convertTransparentValueToBackgroundAlpha(float value) {
        return (int) (255 * value);
    }

    /**
     * @param value int to set the text size of the index bar
     */
    public void setIndexTextSize(int value) {
        setIndexTextSize = value;
    }

    /**
     * @param value float to set the width of the index bar
     */
    public void setIndexbarWidth(float value) {
        mIndexbarWidth = value;
    }

    /**
     * @param value float to set the margin of the index bar
     */
    public void setIndexbarMargin(float value) {
        mIndexbarMarginV = value;
    }

    /**
     * @param value int to set preview padding
     */
    public void setPreviewPadding(int value) {
        setPreviewPadding = value;
    }

    /**
     * @param value int to set the radius of the index bar
     */
    public void setIndexBarCornerRadius(int value) {
        setIndexBarCornerRadius = value;
    }

    /**
     * @param value float to set the transparency of the color for index bar
     */
    public void setIndexBarTransparentValue(float value) {
        indexbarBackgroudAlpha = convertTransparentValueToBackgroundAlpha(value);
    }

    /**
     * @param typeface Typeface to set the typeface of the preview & the index bar
     */
    public void setTypeface(Typeface typeface) {
        setTypeface = typeface;
    }

    /**
     * @param shown boolean to show or hide the index bar
     */
    public void setIndexBarVisibility(boolean shown) {
        setIndexBarVisibility = shown;
    }

    /**
     * @param shown boolean to show or hide the preview box
     */
    public void setPreviewVisibility(boolean shown) {
        previewVisibility = shown;
    }

    /**
     * @param value int to set the text size of the preview box
     */
    public void setPreviewTextSize(int value) {
        setPreviewTextSize = value;
    }

    /**
     * @param color The color for the preview box
     */
    public void setPreviewColor(@ColorInt int color) {
        previewBackgroundColor = color;
    }

    /**
     * @param color The text color for the preview box
     */
    public void setPreviewTextColor(@ColorInt int color) {
        previewTextColor = color;
    }

    /**
     * @param value float to set the transparency value of the preview box
     */
    public void setPreviewTransparentValue(float value) {
        previewBackgroudAlpha = convertTransparentValueToBackgroundAlpha(value);
    }

    /**
     * @param color The color for the scroll track
     */
    public void setIndexBarColor(@ColorInt int color) {
        indexbarBackgroudColor = color;
    }

    /**
     * @param color The text color for the mBgCircleColor
     */
    public void setBgCircleColor(@ColorInt int color) {
        mBgCircleColor = color;
    }

    /**
     * @param color The text color for the index bar
     */
    public void setIndexBarTextColor(@ColorInt int color) {
        indexbarTextColor = color;
    }

    /**
     * @param color The text color for the index bar
     */
    public void setIndexBarHighLateTextColor(@ColorInt int color) {
        indexbarHighLateTextColor = color;
    }

    /**
     * @param shown boolean to show or hide the index bar
     */
    public void setIndexBarHighLateTextVisibility(boolean shown) {
        setSetIndexBarHighLateTextVisibility = shown;
    }

    public void updatePosition(int position) {
        mSelectSection = position;
        mRecyclerView.invalidate();
    }
}