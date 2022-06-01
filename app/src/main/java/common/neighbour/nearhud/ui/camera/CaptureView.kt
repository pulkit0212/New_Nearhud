package common.neighbour.nearhud.ui.camera

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.makeramen.roundedimageview.RoundedImageView
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import common.neighbour.nearhud.R

@SuppressLint("ClickableViewAccessibility")
class CaptureView(context: Context, attributeSet: AttributeSet) :
    ConstraintLayout(context, attributeSet) {

    interface EventCallbacks {
        fun onCaptureClick(view: CaptureView)
        fun onCaptureLongClick(view: CaptureView)
        fun onSecondElapsed(seconds: Int)
        fun onVideoEnds(view: CaptureView)
        fun onGalleryCLick(view: CaptureView)
        fun onCameraToggleClick(view: CaptureView)
    }

    private val morphButtonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFB62C")
        style = Paint.Style.FILL
    }
    private val roundButton: RoundedImageView
    var eventCallbacks: EventCallbacks? = null
    private val progressView: CircularProgressBar
    private var firstActionDown = 0L
    var currentProgress = 0f
    private var videoStarted = false
    private var timeElapsedSeconds = 0
    private var maxTimeSeconds = 60
    private var morphButtonSize = 0
    private var locked = false
    private var revolutions = 0
    private var progressPaused = false
    private val lockButton: RoundedImageView
    private val lockButtonBackground: RoundedImageView
    private var lockThresholdReached = false

    private val runnable = Runnable {
        currentProgress += maxTimeSeconds
        // println(currentProgress)
        if (currentProgress.toInt() / (1000 * (timeElapsedSeconds + 1)) == 1) {
            ++timeElapsedSeconds
            println(timeElapsedSeconds)
            eventCallbacks?.onSecondElapsed(timeElapsedSeconds)
        }
        progress()
    }

    init {
        setWillNotDraw(false)
        val layoutInflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.argear_capture_view, this, true)
        progressView = view.findViewById(R.id.circular_progress_bar)
        roundButton = view.findViewById(R.id.round_button)
        val leftIcon = view.findViewById<ImageView>(R.id.left_icon)
        val rightIcon = view.findViewById<ImageView>(R.id.right_icon)
        leftIcon.setOnClickListener {
            eventCallbacks?.onGalleryCLick(this)
        }
        rightIcon.setOnClickListener {
            it.animate().rotation(if (it.rotation == 360f) -360f else 360f).start()
            eventCallbacks?.onCameraToggleClick(this)
        }
        lockButton = view.findViewById(R.id.lock_button)
        lockButtonBackground = view.findViewById(R.id.lock_button_background)
        val layoutParams = roundButton.layoutParams
        layoutParams.height = 1
        layoutParams.width = 1
        roundButton.layoutParams = layoutParams
        progressView.progressMax = (maxTimeSeconds * 1000).toFloat()
        progressView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    firstActionDown = System.currentTimeMillis()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (System.currentTimeMillis() - firstActionDown > 300) {
                        if (!videoStarted) {
                            progress()
                            rightIcon.visibility = View.GONE
                            leftIcon.visibility = View.GONE
                            lockButton.visibility = View.VISIBLE
                            roundButton.visibility = View.VISIBLE
                            circleScaleUp()
                            videoStarted = true
                            eventCallbacks?.onCaptureLongClick(this)
                        }
                    }
                    if (event.rawX < leftIcon.x + leftIcon.width && videoStarted) {
                        if (!lockThresholdReached) {
                            lockButton.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.locked_lock
                                )
                            )
                            lockThresholdReached = true
                            circleScaleDown()
                            lockButtonScaleUp()
                        }
                    } else if (event.rawX > progressView.x) {
                        if (lockThresholdReached && !locked) {
                            lockThresholdReached = false
                            lockButton.setImageDrawable(
                                ContextCompat.getDrawable(
                                    context,
                                    R.drawable.unlocked_lock
                                )
                            )
                            circleScaleUp()
                            lockButtonScaleDown()
                        }
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    if (videoStarted) {
                        if (lockThresholdReached) {
                            morphToRoundRect()
                            locked = true
                        }
                        if(event.rawX > progressView.x) {
                            progressPaused = true
                            morphToRoundRect()
                            eventCallbacks?.onVideoEnds(this)
                            reset()
                        }
                    } else {
                        eventCallbacks?.onCaptureClick(this)
                        reset()
                    }
                    true
                }
                else -> true
            }
        }
    }

    private fun reset() {
        firstActionDown = 0L
        currentProgress = 0f
        videoStarted = false
        timeElapsedSeconds = 0
        maxTimeSeconds = 15
        morphButtonSize = 0
        locked = false
        revolutions = 0
        lockThresholdReached = false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        morphButtonSize = progressView.width - progressView.width / 10
    }

    private fun lockButtonScaleDown() {
        val animator = ValueAnimator.ofFloat(lockButton.width * 1.5f, 1f)
        animator.duration = 300
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Float
            lockButtonBackground.cornerRadius = animatedValue / 2
            val layoutParams = lockButtonBackground.layoutParams
            layoutParams.height = (animatedValue).toInt()
            layoutParams.width = (animatedValue).toInt()
            lockButtonBackground.layoutParams = layoutParams
        }
        animator.start()
    }

    private fun lockButtonScaleUp() {
        lockButtonBackground.visibility = View.VISIBLE
        val animator = ValueAnimator.ofFloat(1f, lockButton.width * 1.5f)
        animator.duration = 300
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Float
            lockButtonBackground.cornerRadius = animatedValue / 2
            val layoutParams = lockButtonBackground.layoutParams
            layoutParams.height = (animatedValue).toInt()
            layoutParams.width = (animatedValue).toInt()
            lockButtonBackground.layoutParams = layoutParams
        }
        animator.start()
    }

    private fun circleScaleDown() {
        val animator = ValueAnimator.ofFloat(roundButton.width / 2f, 1f)
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Float
            roundButton.cornerRadius = animatedValue
            val layoutParams = roundButton.layoutParams
            layoutParams.height = (animatedValue).toInt()
            layoutParams.width = (animatedValue).toInt()
            roundButton.layoutParams = layoutParams
        }
        animator.start()
    }

    private fun circleScaleUp() {
        val end = progressView.width - progressView.width / 5f
        val animator = ValueAnimator.ofFloat(1f, end)
        animator.duration = 500
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Float
            roundButton.cornerRadius = animatedValue
            val layoutParams = roundButton.layoutParams
            layoutParams.height = (animatedValue).toInt()
            layoutParams.width = (animatedValue).toInt()
            roundButton.layoutParams = layoutParams
        }
        animator.start()
    }

    private fun morphToRoundRect() {
        val end = progressView.width / 12f
        val animator = ValueAnimator.ofFloat(1f, end)
        animator.addUpdateListener {
            val animatedValue = it.animatedValue as Float
            val layoutParams = roundButton.layoutParams
            layoutParams.height = (animatedValue * 3).toInt()
            layoutParams.width = (animatedValue * 3).toInt()
            roundButton.layoutParams = layoutParams
            roundButton.cornerRadius = animatedValue
        }
        animator.start()
    }

    private fun progress() {
        if (timeElapsedSeconds == maxTimeSeconds * (revolutions + 1)) {
            ++revolutions
            if (locked) {
                currentProgress = 0f
                progressView.progress = currentProgress
                handler.postDelayed(runnable, maxTimeSeconds.toLong())
            } else {
                eventCallbacks?.onVideoEnds(this)
                handler.removeCallbacks(runnable)
            }
        } else if(!progressPaused){
            progressView.progress = currentProgress
            handler.postDelayed(runnable, maxTimeSeconds.toLong())
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        handler.removeCallbacks(runnable)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }
}
