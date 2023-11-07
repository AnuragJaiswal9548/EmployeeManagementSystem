package com.example.roomdatabaseexampleone

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.room.Room
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class MainActivity : AppCompatActivity() {


    lateinit var listView: ListView
    lateinit var database: ContactDatabase

    //notification
    lateinit var notificationManager : NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    lateinit var pendingIntent: PendingIntent
    lateinit var soundUri: Uri
    lateinit var audioAttr: AudioAttributes
    private val channelId = "My Channel ID"
    private val description = "New Detail Added"
    private val title="Details sheet has some changes"

    val myKey = "Remote Key"
    val notificationId =1234
    // notification


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //notification
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        //
        database = Room.databaseBuilder(applicationContext, ContactDatabase::class.java, "contactDb")
                .fallbackToDestructiveMigration().build()

        val display = findViewById<Button>(R.id.display)
        listView = findViewById(R.id.listView)

        listView.setOnItemLongClickListener { parent, view, position, id ->
            val view = parent.get(position)
            val id = view.findViewById<TextView>(R.id.idListItem).text.toString().toLong()
            val name = view.findViewById<TextView>(R.id.nameListItem).text.toString()
            val phone = view.findViewById<TextView>(R.id.phoneListItem).text.toString().toLong()
            val age = view.findViewById<TextView>(R.id.ageListItem).text.toString()

            var builder = AlertDialog.Builder(this)
            builder.setTitle("Edit")
            var linearLayout = LinearLayout(this)
            linearLayout.orientation = LinearLayout.VERTICAL

            val idView = EditText(this)
            idView.setText(id.toString())
            linearLayout.addView(idView)

            val nameView = EditText(this)
            nameView.text.clear()
            nameView.setText(name)
            linearLayout.addView(nameView)

            val phoneView = EditText(this)
            phoneView.text.clear()
            phoneView.setText(phone.toString())
            linearLayout.addView(phoneView)

            val ageView = EditText(this)
            ageView.text.clear()
            ageView.setText(age)
            linearLayout.addView(ageView)

            //Toast.makeText(this, "Updated $updatedName $updatedPhone", Toast.LENGTH_SHORT).show()

            builder.setView(linearLayout)

            builder.setPositiveButton("Edit", DialogInterface.OnClickListener { dialog, which ->
                val updatedName = nameView.text.toString()
                val updatedPhone = phoneView.text.toString().toLong()
                val updatedAge = ageView.text.toString()
                GlobalScope.launch {
                    database.ContactDAO().update(Contact(id, updatedName, updatedPhone, updatedAge))
                }
                Toast.makeText(
                    this,
                    "Updated $updatedName $updatedPhone $updatedAge",
                    Toast.LENGTH_SHORT
                ).show()
            })

            builder.setNegativeButton(
                "Cancel",
                DialogInterface.OnClickListener { dialog, which -> dialog.cancel() })

            builder.show()

            return@setOnItemLongClickListener true
        }
        listView.setOnItemClickListener { parent, view, position, id ->
            val view = parent.get(position)
            val id = view.findViewById<TextView>(R.id.idListItem).text.toString().toLong()
            val name = view.findViewById<TextView>(R.id.nameListItem).text.toString()
            val phone = view.findViewById<TextView>(R.id.phoneListItem).text.toString().toLong()
            val age = view.findViewById<TextView>(R.id.ageListItem).text.toString()
            GlobalScope.launch {
                database.ContactDAO().delete(Contact(id, name, phone, age))
            }
        }

        display.setOnClickListener() {
            getData(it)
        }
        var btn = findViewById<Button>(R.id.addBtn)
        btn.setOnClickListener() {
            var id = findViewById<EditText>(R.id.idIn)
            var name = findViewById<EditText>(R.id.nameIn)
            var phone = findViewById<EditText>(R.id.phoneID)
            var age = findViewById<EditText>(R.id.ageID)
            GlobalScope.launch { database.ContactDAO().insert(
                    Contact(id.text.toString().toLong(), name.text.toString(),phone.text.toString().toLong(), age.text.toString()))


//                showNotification()
                //clear text after saving
                name.text.clear()
                phone.text.clear()
                age.text.clear()
                id.text.clear()
                //
            }
            val intent=Intent(this@MainActivity,SplashScreen::class.java)
            startActivity(intent)

            // notification
            val intent1 = Intent(this, NotificationViewExample::class.java)
            pendingIntent = PendingIntent.getActivity(this,0,intent1,PendingIntent.FLAG_MUTABLE)

            soundUri = Uri.parse(
                ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+
                        applicationContext.packageName+"/"+R.raw.notification)
            audioAttr = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            myNotificationChannel()
            notificationManager.notify(notificationId,builder.build())
            // notification

        }
    }
    private fun myNotificationChannel(){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            run {
                notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(false)
                notificationChannel.setSound(soundUri, audioAttr)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(this, channelId)
                    .setSmallIcon(R.drawable.icon)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources,R.drawable.icon))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)

            }
        else
            run {
                builder = Notification.Builder(this, channelId)
                    .setSmallIcon(android.R.drawable.btn_star)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.drawable.icon))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            }
    }

    fun getData(view: View) {
        database.ContactDAO().getContact().observe(this) {
            val adapter = MyAdapter(this, R.layout.list_item, it)
            listView.adapter = adapter
        }
    }
}