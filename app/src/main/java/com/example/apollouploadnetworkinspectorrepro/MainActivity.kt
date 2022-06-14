package com.example.apollouploadnetworkinspectorrepro

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.DefaultUpload
import com.apollographql.apollo3.mockserver.MockServer
import com.apollographql.apollo3.mockserver.enqueue
import kotlinx.coroutines.runBlocking
import okio.Buffer

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        networkCall()
    }

    private fun networkCall() {
        runBlocking {
            val mockServer = MockServer()
            mockServer.enqueue("""{
                    "data": {
                        "upload": true
                    }
                }"""
            )
            val url = mockServer.url().replace("::", "localhost")
            val apolloClient = ApolloClient.Builder().serverUrl(url).build()
            val upload = DefaultUpload.Builder()
                .content(Buffer().writeUtf8("contents"))
                .contentLength("contents".length.toLong())
                .build()
            try {
                val data = apolloClient.mutation(UploadMutation(upload)).execute().dataAssertNoErrors
                println("Results = ${data.upload}")
            } catch (e: Exception) {
                e.printStackTrace() // <- happens when using the Network Inspector
            }
            mockServer.stop()
        }
    }
}
