import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

import com.adrpien.musicplayerapp.PlayerService
import com.adrpien.musicplayerapp.R

class PlayerNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if(intent != null) {
            val intentAction = intent.action
            when(intentAction){
                "PLAY_ACTION" -> {
                    val playIntent = Intent(context, PlayerService::class.java)
                    playIntent.action = context?.getString(R.string.PLAY_ACTION)
                    context?.startService(playIntent)
                }
                "BACK_ACTION" -> {
                    val backIntent = Intent(context, PlayerService::class.java)
                    backIntent.action = context?.getString(R.string.BACK_ACTION)
                    context?.startService(backIntent)
                }
                "NEXT_ACTION" -> {
                    val nextIntent = Intent(context, PlayerService::class.java)
                    nextIntent.action = context?.getString(R.string.NEXT_ACTION)
                    context?.startService(nextIntent)
                }
                "PAUSE_ACTION" -> {
                    val pauseIntent = Intent(context, PlayerService::class.java)
                    pauseIntent.action = context?.getString(R.string.PAUSE_ACTION)
                    context?.startService(pauseIntent)
                }
            }
        }

    }
}