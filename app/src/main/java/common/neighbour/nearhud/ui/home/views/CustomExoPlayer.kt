package common.neighbour.nearhud.ui.home.views

import android.widget.FrameLayout
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import common.neighbour.nearhud.ui.home.views.ExoPlayerHelper
import common.neighbour.nearhud.ui.home.views.ExoPlayerManager
import com.google.android.exoplayer2.ui.PlayerView

class CustomExoPlayer: FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
//        const val ID = 0x11203
    }

    var url: String? = "https://firebasestorage.googleapis.com/v0/b/nearhud-dc6c7.appspot.com/o/images_videos%2F7014586715%2F1637724434516?alt=media&token=57d90b10-dcf7-46d6-80ea-98eccb408bed"
    var imageView: ImageView? = null
    var isMute: Boolean = true
        set(value) {
            field = value
            if (playerView != null && playerView!!.tag != null && playerView!!.tag is ExoPlayerManager) {
                val masterExoPlayerHelper = (playerView!!.tag as ExoPlayerManager)
                masterExoPlayerHelper.isMute = value
                if (value)
                    masterExoPlayerHelper.exoPlayerHelper.mute()
                else
                    masterExoPlayerHelper.exoPlayerHelper.unMute()
            }
        }

    var playerView: PlayerView? = null

    fun addPlayer(playerView: PlayerView, autoPlay: Boolean) {
        if (this.playerView == null) {
            this.playerView = playerView
            addView(playerView)
            //This autoplay flag is used so we don't hide image view
            if (autoPlay) {
//                imageView?.animate()?.setDuration(0)?.alpha(0f)
            }
        }
    }

    fun removePlayer() {
        if (playerView != null) {
            removeView(playerView)
            playerView = null
            imageView?.visibility = View.VISIBLE
            imageView?.animate()?.setDuration(0)?.alpha(1f)
            listener?.onStop()
        }
    }

    override fun removeView(view: View?) {
        super.removeView(view)
        if (view is PlayerView) {
            playerView = null
            imageView?.visibility = View.VISIBLE
            imageView?.animate()?.setDuration(0)?.alpha(1f)
        }
    }

    fun hideThumbImage(thumbHideDelay: Long) {
        imageView?.animate()?.setStartDelay(thumbHideDelay)?.setDuration(0)?.alpha(0f)
    }

    var listener: ExoPlayerHelper.Listener? = null
}