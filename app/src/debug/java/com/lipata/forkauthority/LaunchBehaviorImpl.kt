import android.app.Activity
import android.content.Intent
import android.widget.TextView
import com.lipata.forkauthority.BuildConfig
import com.lipata.forkauthority.LaunchBehavior
import com.lipata.forkauthority.R
import com.lipata.forkauthority.businesslist.BusinessListActivity
import com.lipata.forkauthority.poll.PollActivity

class LaunchBehaviorImpl : LaunchBehavior {
    override fun invoke(activity: Activity) {
        if (!BuildConfig.DEBUG) {
            launchRestaurantListFeature(activity)
            activity.finishAffinity()
        } else {
            showDebugActivity(activity)
        }
    }
    private fun showDebugActivity(activity: Activity) {
        activity.setContentView(R.layout.activity_debug)

        activity.findViewById<TextView>(R.id.textViewRestaurantList).setOnClickListener {
            launchRestaurantListFeature(activity)
        }

        activity.findViewById<TextView>(R.id.textViewPoll).setOnClickListener {
            launchPollActivity(activity)
        }
    }

    private fun launchRestaurantListFeature(activity: Activity) {
        activity.startActivity(Intent(activity, BusinessListActivity::class.java))
    }

    private fun launchPollActivity(activity: Activity) {
        activity.startActivity(Intent(activity, PollActivity::class.java))
    }
}