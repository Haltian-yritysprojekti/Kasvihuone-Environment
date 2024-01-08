package com.example.kasvihuone_environment



import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONException

class DoorStatusService : Service() {

    private val handler = Handler()
    private lateinit var checkDoorStatusRunnable: Runnable
    private val apiRequest: ApiRequest = ApiRequest(this)

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        checkDoorStatusRunnable = Runnable {
            Log.d("DoorStatusService", "Checking door status...")
            // Check door status here and show notification if unlocked
            apiRequest.getDoorStatus(
                onSuccess = { doorStatus ->
                    if (doorStatus == 0) {
                        showNotification()
                    }
                    handler.postDelayed(this@DoorStatusService.checkDoorStatusRunnable, 60 * 1000)
                },
                onError = {
                    // Handle network request failure
                    handler.postDelayed(this@DoorStatusService.checkDoorStatusRunnable, 60 * 1000)
                }
            )
        }

        // Start the initial check
        handler.post(checkDoorStatusRunnable)

        return START_STICKY
    }

    private fun showNotification() {
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "door_status_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Door Status Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setContentTitle("Ovi-ilmoitus")
            .setContentText("Kasvihuoneen ovi on auki!")
            .setSmallIcon(R.drawable.kasvihuone_environment_logo) // Replace with your app's icon
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notification: Notification = notificationBuilder.build()

        notificationManager.notify(1, notification)
    }
}

class ApiRequest(private val context: Context) {

    fun getDoorStatus(onSuccess: (Int) -> Unit, onError: () -> Unit) {
        val url = "https://std7prto3fhwxwtyl24eg4fofu0gzazj.lambda-url.us-east-1.on.aws/"

        val request = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                // Parse the response and extract door status
                val doorStatus = parseDoorStatus(response)
                onSuccess.invoke(doorStatus)
            },
            {
                onError.invoke()
            }
        )

        // Add the request to the request queue
        Volley.newRequestQueue(context.applicationContext).add(request)
    }

    private fun parseDoorStatus(response: String): Int {
        try {
            val jsonArray = JSONArray(response)
            if (jsonArray.length() > 0) {
                // Assuming the "door" value is present in the first object of the array
                return jsonArray.getJSONObject(0).optInt("door", 1)
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        // If an error occurs during parsing, assume the door is closed for testing
        return 1
    }

}