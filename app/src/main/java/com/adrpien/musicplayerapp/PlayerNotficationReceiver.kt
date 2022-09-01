import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class PlayerNotificationReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // Toast.makeText(context, "Play/Pause", Toast.LENGTH_SHORT).show()
        if(intent != null) {
            val intentAction = intent.action
            when(intentAction){
                "PLAY_ACTION" -> {
                    Toast.makeText(context, "PLAY_ACTION", Toast.LENGTH_SHORT).show()

                }
                "BACK_ACTION" -> {
                    // TODO Notification back button implementation
                    Toast.makeText(context, "BACK_ACTION", Toast.LENGTH_SHORT).show()

                }
                "NEXT_ACTION" -> {
                    // TODO Notification next button implementation
                    Toast.makeText(context, "NEXT_ACTION", Toast.LENGTH_SHORT).show()

                }
            }
        }

    }
}