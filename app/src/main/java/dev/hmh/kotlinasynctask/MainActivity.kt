package dev.hmh.kotlinasynctask

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var progressDialog: ProgressDialog
    lateinit var arr_getCity: ArrayList<String>
    lateinit var arr_getCityNo: ArrayList<String>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressDialog = ProgressDialog(this)


        /*   final Handler handler = new Handler();
        final int delay = 5000; //milliseconds

        handler.postDelayed(new Runnable() {
            public void run() {
                new BackgroundGetParentCodeTask().execute(getString(R.string.AllParentCodeList));
                //do something
                handler.postDelayed(this, delay);
            }
        }, delay);*/

        BackgroundGetCityTask().execute("https://aampower.app/data/getcity.aspx")
    }

    @SuppressLint("StaticFieldLeak")
    private inner class BackgroundGetCityTask : AsyncTask<String?, Void?, String?>() {
        override fun onPreExecute() {
            super.onPreExecute()
            progressDialog.setMessage("Please wait...")
            progressDialog.setCanceledOnTouchOutside(false)
            progressDialog.show()
        }

        override fun doInBackground(vararg strings: String?): String? {

            try {
                val url = URL(strings[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true

                val inputStream = connection.inputStream
                val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                val stringBuffer = StringBuffer()
                var line: String? = ""
                while (bufferedReader.readLine().also { line = it } != null) {
                    stringBuffer.append(line)
                }
                return stringBuffer.toString()

            } catch (e: JSONException) {
                e.printStackTrace()
                return ""
            }
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)
            progressDialog.dismiss()
            if (result != "") {
                arr_getCity = ArrayList<String>()
                arr_getCityNo = ArrayList<String>()
                val jsonArray = JSONArray(result)
                for (i in 0 until jsonArray.length()) {
                    val finalJasonObject = jsonArray.getJSONObject(i)
                    arr_getCity.add(finalJasonObject.getString("CityName"))
                    arr_getCityNo.add(finalJasonObject.getString("CityNo"))
                }
                if (arr_getCity.size > 0) {
                    Toast.makeText(
                        this@MainActivity,
                        "$arr_getCity",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@MainActivity, "Record is Empty", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this@MainActivity, "Network Error...!", Toast.LENGTH_SHORT).show()
            }


        }
    }
}