package uk.co.senab.photoview.sample;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * Layout that provides pinch-zooming of content. This view should have exactly one child
 * view containing the content. It is based upon https://gist.github.com/anorth/9845602
 * and has been made compatible with beeing a child inside a ViewPager: if the user tries
 * to scroll past the childs egdes the event will be handled by the outer pager if this layout is
 * nested inside a ViewPager
 */
public class PanZoomChildrenLayout extends FrameLayout implements ScaleGestureDetector.OnScaleGestureListener {

    private boolean allowParentInterceptOnEdge=true;
    private boolean blockParentIntercept=false;

    static final int EDGE_NONE = -1;
    static final int EDGE_LEFT = 0;
    static final int EDGE_RIGHT = 1;
    static final int EDGE_BOTH = 2;

    private int scrollEdge=EDGE_BOTH;


    private enum Mode {
        NONE,
        DRAG,
        ZOOM
    }

    private static final String TAG = "ZoomLayout";
    private static final float MIN_ZOOM = 1.0f;
    private static final float MAX_ZOOM = 4.0f;

    private Mode mode = Mode.NONE;
    private float scale = 1.0f;
    private float lastScaleFactor = 0f;

    // Where the finger first  touches the screen
    private float startX = 0f;
    private float startY = 0f;

    // How much to translate the canvas
    private float dx = 0f;
    private float dy = 0f;
    private float prevDx = 0f;
    private float prevDy = 0f;

    public PanZoomChildrenLayout(Context context) {
        super(context);
        init(context);
    }

    public PanZoomChildrenLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PanZoomChildrenLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        final ScaleGestureDetector scaleDetector = new ScaleGestureDetector(context, this);
        this.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_DOWN:
                        Log.i(TAG, "DOWN");
                        if (scale > MIN_ZOOM) {
                            mode = Mode.DRAG;
                            startX = motionEvent.getX() - prevDx;
                            startY = motionEvent.getY() - prevDy;
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if (mode == Mode.DRAG) {
                            dx = motionEvent.getX() - startX;
                            dy = motionEvent.getY() - startY;
                        }
                        break;
                    case MotionEvent.ACTION_POINTER_DOWN:
                        mode = Mode.ZOOM;
                        break;
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = Mode.DRAG;
                        break;
                    case MotionEvent.ACTION_UP:
                        Log.i(TAG, "UP");
                        mode = Mode.NONE;
                        prevDx = dx;
                        prevDy = dy;
                        break;
                }
                scaleDetector.onTouchEvent(motionEvent);

                if ((mode == Mode.DRAG && scale >= MIN_ZOOM) || mode == Mode.ZOOM) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    float maxDx = (child().getWidth() - (child().getWidth() / scale)) / 2 * scale;
                    float maxDy = (child().getHeight() - (child().getHeight() / scale))/ 2 * scale;
                    dx = Math.min(Math.max(dx, -maxDx), maxDx);
                    dy = Math.min(Math.max(dy, -maxDy), maxDy);
                    Log.i(TAG, "Width: " + child().getWidth() + ", scale " + scale + ", dx " + dx
                            + ", max " + maxDx);
                    applyScaleAndTranslation();
                }

                return true;
            }
        });
    }

    // ScaleGestureDetector

    @Override
    public boolean onScaleBegin(ScaleGestureDetector scaleDetector) {
        Log.i(TAG, "onScaleBegin");
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector scaleDetector) {
        float scaleFactor = scaleDetector.getScaleFactor();
        Log.i(TAG, "onScale" + scaleFactor);
        if (lastScaleFactor == 0 || (Math.signum(scaleFactor) == Math.signum(lastScaleFactor))) {
            scale *= scaleFactor;
            scale = Math.max(MIN_ZOOM, Math.min(scale, MAX_ZOOM));
            lastScaleFactor = scaleFactor;
        } else {
            lastScaleFactor = 0;
        }
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector scaleDetector) {
        Log.i(TAG, "onScaleEnd");
    }


    private void splitTranslationXOnEdges() {
        ViewParent parent = getParent();
        if (allowParentInterceptOnEdge && mode==Mode.DRAG && !blockParentIntercept) {
            if (scrollEdge == EDGE_BOTH
                    || (scrollEdge == EDGE_LEFT && dx >= 1f)
                    || (scrollEdge == EDGE_RIGHT && dx <= -1f)) {
                if (null != parent) {
                    parent.requestDisallowInterceptTouchEvent(false);
                }
            }
        } else {
            if (null != parent) {
                parent.requestDisallowInterceptTouchEvent(true);
            }
        }
    }


    private void applyScaleAndTranslation() {
        child().setScaleX(scale);
        child().setScaleY(scale);
        float oldTranslationX=child().getTranslationX();
        splitTranslationXOnEdges();
        float newTranslationX=child().getTranslationX();
        Log.d("SPLIT","oldTranslationX: "+oldTranslationX);
        Log.d("SPLIT","newTranslationX: "+newTranslationX);
        if (oldTranslationX==newTranslationX) {
            requestDisallowInterceptTouchEvent(false);
        } else {
            requestDisallowInterceptTouchEvent(true);
        }
        child().setTranslationY(dy);
    }

    private View child() {
        return getChildAt(0);
    }
}