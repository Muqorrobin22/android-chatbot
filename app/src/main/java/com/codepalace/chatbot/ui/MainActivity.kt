package com.codepalace.chatbot.ui

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.TextView
//import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.codepalace.chatbot.R
import com.codepalace.chatbot.controller.DiseaseService
import com.codepalace.chatbot.data.Message
import com.codepalace.chatbot.model.DiseaseSymptomsModelItem
import com.codepalace.chatbot.utils.Constants.RECEIVE_ID
import com.codepalace.chatbot.utils.Constants.SEND_ID
import com.codepalace.chatbot.utils.BotResponse
import com.codepalace.chatbot.utils.Constants.OPEN_GOOGLE
import com.codepalace.chatbot.utils.Constants.OPEN_SEARCH
import com.codepalace.chatbot.utils.Time
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.coba.view.*
import kotlinx.coroutines.*
//import okhttp3.*
import retrofit2.Call
import org.w3c.dom.Text
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

const val BASE_URL = "http://10.0.2.2:3000/"

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    //You can ignore this messageList if you're coming from the tutorial,
    // it was used only for my personal debugging
    var messagesList = mutableListOf<Message>()
    var responseMessegae = mutableListOf<String>()

    private lateinit var adapter: MessagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        initPython()
        setContentView(R.layout.activity_main)

        recyclerView()

        clickEvents()

        customBotMessage(welcomeWordPython())

        getDiseaseData()
    }

    fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

    private fun getDiseaseData() {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(DiseaseService::class.java)

        val retrofitData = retrofitBuilder.getDiseases()

        retrofitData.enqueue(object : Callback<List<DiseaseSymptomsModelItem>?> {
            override fun onResponse(
                call: Call<List<DiseaseSymptomsModelItem>?>,
                response: Response<List<DiseaseSymptomsModelItem>?>
            ) {
                val respondBody = response.body()!!

                val myStringBuilder = StringBuilder()
                for (myData in respondBody) {
                    myStringBuilder.append(myData.disease_name)
                    myStringBuilder.append("\n")
                    myStringBuilder.append(myData.value_weight)
                }

//                editTextTextPersonName.text = myStringBuilder.toString().toEditable()

                customBotMessage(myStringBuilder.toString())

                Log.d(TAG, "Sukses succeed: " + respondBody)
            }

            override fun onFailure(call: Call<List<DiseaseSymptomsModelItem>?>, t: Throwable) {
                Log.d(TAG, "onFailure: " +t.message)
            }
        })
    }

    private fun clickEvents() {

        //Send a message
        btn_send.setOnClickListener {
            sendMessage()
        }

        //Scroll back to correct position when user clicks on text view
        et_message.setOnClickListener {
            GlobalScope.launch {
                delay(100)

                withContext(Dispatchers.Main) {
                    rv_messages.scrollToPosition(adapter.itemCount - 1)

                }
            }
        }
    }

    private fun welcomeWordPython(): String {
        val python = Python.getInstance()
        val pythonFiles = python.getModule("welcomeWord")
        return pythonFiles.callAttr("welcome").toString()
    }

    private fun initPython() {
        if (! Python.isStarted()) {
            Python.start( AndroidPlatform(this));
        }
    }

    private fun recyclerView() {
        adapter = MessagingAdapter()
        rv_messages.adapter = adapter
        rv_messages.layoutManager = LinearLayoutManager(applicationContext)

    }

    override fun onStart() {
        super.onStart()
        //In case there are messages, scroll to bottom when re-opening app
        GlobalScope.launch {
            delay(100)
            withContext(Dispatchers.Main) {
                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }

    private fun sendMessage() {
        val message = et_message.text.toString().toLowerCase()
        val timeStamp = Time.timeStamp()

        if (message.isNotEmpty()) {
            //Adds it to our local list
            messagesList.add(Message(message, SEND_ID, timeStamp))
            et_message.setText("")

            adapter.insertMessage(Message(message, SEND_ID, timeStamp))
            rv_messages.scrollToPosition(adapter.itemCount - 1)

            botResponse(message)
        }
    }

    private fun botResponse(message: String) {
        val timeStamp = Time.timeStamp()

        GlobalScope.launch {
            //Fake response delay
            delay(1000)

            withContext(Dispatchers.Main) {
                //Gets the response
                val response = BotResponse.basicResponses(message)

                //Adds it to our local list
                messagesList.add(Message(response, RECEIVE_ID, timeStamp))

                //Inserts our message into the adapter
                adapter.insertMessage(Message(response, RECEIVE_ID, timeStamp))

                //Scrolls us to the position of the latest message
                rv_messages.scrollToPosition(adapter.itemCount - 1)

                //Starts Google
                when (response) {
                    OPEN_GOOGLE -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        site.data = Uri.parse("https://www.google.com/")
                        startActivity(site)
                    }
                    OPEN_SEARCH -> {
                        val site = Intent(Intent.ACTION_VIEW)
                        val searchTerm: String? = message.substringAfterLast("search")
                        site.data = Uri.parse("https://www.google.com/search?&q=$searchTerm")
                        startActivity(site)
                    }

                }
            }
        }
    }

    private fun customBotMessage(message: String) {

        GlobalScope.launch {
            delay(1000)
            withContext(Dispatchers.Main) {
                val timeStamp = Time.timeStamp()
                messagesList.add(Message(message, RECEIVE_ID, timeStamp))
                adapter.insertMessage(Message(message, RECEIVE_ID, timeStamp))

                rv_messages.scrollToPosition(adapter.itemCount - 1)
            }
        }
    }
}