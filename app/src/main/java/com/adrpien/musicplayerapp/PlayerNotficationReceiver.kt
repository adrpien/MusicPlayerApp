import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.adrpien.musicplayerapp.App.Companion.mediaPlayer
import com.adrpien.musicplayerapp.PlayerService

class PlayerNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {


        if(intent != null) {
            val intentAction = intent.action
            when(intentAction){
                "PLAY_ACTION" -> {
                    // HOW TO GET ACCESS TO MEDIA PLAYER IN SERVICE ?????
                    mediaPlayer.start()
                    Toast.makeText(context, "PLAY_ACTION", Toast.LENGTH_SHORT).show()
                    // TODO Change image button image in fragment

                }
                "BACK_ACTION" -> {
                    // TODO Notification back button implementation
                    Toast.makeText(context, "BACK_ACTION", Toast.LENGTH_SHORT).show()

                }
                "NEXT_ACTION" -> {
                    // TODO Notification next button implementation
                    Toast.makeText(context, "NEXT_ACTION", Toast.LENGTH_SHORT).show()

                }
                "PAUSE_ACTION" -> {
                    // TODO Notification pause button implementation
                    Toast.makeText(context, "PAUSE_ACTION", Toast.LENGTH_SHORT).show()

                }
            }
        }

    }
}