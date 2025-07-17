package com.example.week_6_random_pet_lab

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import org.json.JSONException // Important for parsing arrays
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var petImageView: ImageView
    private lateinit var actionButton: Button

    // API URLs
    private val dogApiUrl = "https://dog.ceo/api/breeds/image/random"
    private val catApiUrl = "https://api.thecatapi.com/v1/images/search"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        petImageView = findViewById(R.id.petImage)
        actionButton = findViewById(R.id.petButton)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupButton()

        // Decide what to load initially.
        // For testing Step 5, let's load a cat image first.
        fetchCatImage()
        // Or, if you want to start with a dog:
        // fetchDogImage()
    }

    private fun fetchDogImage() {
        val client = AsyncHttpClient()
        client[dogApiUrl, object : JsonHttpResponseHandler() { // Use dogApiUrl
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("DogAPI", "Dog Response successful: $json")
                try {
                    // Dog API returns a single object with "message"
                    val petImageURL = json.jsonObject.getString("message")
                    Log.d("DogAPI_URL", "Dog image URL: $petImageURL")

                    Glide.with(this@MainActivity)
                        .load(petImageURL)
                        .fitCenter()
                        .into(petImageView)
                } catch (e: JSONException) { // More specific exception
                    Log.e("DogAPI_ParseError", "Error parsing Dog JSON", e)
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.e("DogAPI_Error", "Status: $statusCode, Response: $errorResponse")
                throwable?.message?.let { Log.e("DogAPI_Throwable", it) }
            }
        }]
    }

    // New function for fetching cat images, as per Step 5
    private fun fetchCatImage() {
        val client = AsyncHttpClient()
        client[catApiUrl, object : JsonHttpResponseHandler() { // Use catApiUrl
            override fun onSuccess(statusCode: Int, headers: Headers, json: JsonHttpResponseHandler.JSON) {
                Log.d("CatAPI", "Cat Response successful: $json")
                try {
                    // Cat API returns a JSON array with one object, containing "url"
                    val resultsJSON = json.jsonArray.getJSONObject(0) // Get the first object from the array
                    val petImageURL = resultsJSON.getString("url")     // Get the URL from that object
                    Log.d("CatAPI_URL", "Cat image URL: $petImageURL")

                    Glide.with(this@MainActivity)
                        .load(petImageURL)
                        .fitCenter()
                        .into(petImageView)
                } catch (e: JSONException) { // More specific exception
                    Log.e("CatAPI_ParseError", "Error parsing Cat JSON", e)
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                errorResponse: String,
                throwable: Throwable?
            ) {
                Log.e("CatAPI_Error", "Status: $statusCode, Response: $errorResponse")
                throwable?.message?.let { Log.e("CatAPI_Throwable", it) }
            }
        }]
    }

    private fun setupButton() {
        actionButton.setOnClickListener {
            // As per Step 5 instructions: "change the call to the fetchDogImage()
            // function in your setupButton() function to fetchCatImage()."
            Log.d("ButtonClick", "Button clicked, fetching new cat image.")
            val randomly = Random.nextInt(2)
            if (randomly==0){
                fetchCatImage()
            }
            else{
                fetchDogImage()
            }
            // If you wanted it to fetch dogs, you'd call fetchDogImage()
        }
    }
}
