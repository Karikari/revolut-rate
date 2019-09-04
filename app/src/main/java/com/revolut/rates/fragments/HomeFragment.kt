package com.revolut.rates.fragments


import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.revolut.rates.R
import com.revolut.rates.RateApplication
import com.revolut.rates.adapters.RateADP
import com.revolut.rates.http.RateHttp
import com.revolut.rates.http.models.Country
import com.revolut.rates.http.models.ResData
import com.revolut.rates.interfaces.keyboardListener
import com.revolut.rates.models.RateMDL
import com.revolut.rates.settings.PrefManager
import com.revolut.rates.settings.UtilsManager
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList


/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment(), keyboardListener {


    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }

    var records = ArrayList<RateMDL>()

    private val req: RateHttp.IRequest by lazy {
        RateHttp.getInstance()
    }

    private val prefs: PrefManager by lazy {
        PrefManager(RateApplication.getContext())
    }

    var countries = hashMapOf<String, Country>()

    var rates = hashMapOf<String, Any>()
    var updated_recorded = ArrayList<RateMDL>()

    var mAdapter = RateADP(records)

    init {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countries = UtilsManager.getCountryData(activity)

        prefs.setAmount("100")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSwipe.isRefreshing = true
        updateRates()

        mSwipe.setOnRefreshListener {
            records.clear()
            rates.clear()
            prefs.setAmount("100")
            updateRates()
        }

        runTimer()


    }

    fun updateRates() {
        req.rates.enqueue(object : Callback<ResData> {

            override fun onFailure(call: Call<ResData>, t: Throwable) {
                mSwipe.isRefreshing = false
                //Log.e(TAG, "Request Errro ${t.message}")
                Snackbar.make(mSwipe, "Connection failed try again", Snackbar.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ResData>, response: Response<ResData>) {
                Log.d(TAG, "Status Code ${response.code()}  URL : ${call.request().url()}")
                if (response.isSuccessful) {
                    val resData = response.body()
                    if (resData != null) {
                        rates = UtilsManager.getRateData(resData.rates.toString())
                    }

                    val record = RateMDL()
                    record.name = "EURO"
                    record.code = "EUR"
                    record.symbol = ""
                    record.rates = 1.0

                    records.add(record)
                    for ((key, value) in rates) {
                        val country = countries.get(key)
                        if (country != null) {
                            val record = RateMDL()
                            record.name = country.name
                            record.code = key
                            record.symbol = country.symbol
                            record.rates = value as Double
                            records.add(record)
                        }
                    }
                    listRates(records)
                    mSwipe.isRefreshing = false
                }
            }
        })
    }

    override fun onSendData(editText: TextInputEditText?) {
        val imm = RateApplication.getContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm!!.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
        imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }


    fun listRates(rates: ArrayList<RateMDL>) {
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        mAdapter = RateADP(rates)
        mAdapter.setHasStableIds(true)
        mAdapter.addRecycler(mRecyclerView,this)
        mRecyclerView.adapter = mAdapter
        mAdapter!!.notifyDataSetChanged()
    }

    fun loadRates() {
        req.rates.enqueue(object : Callback<ResData> {

            override fun onFailure(call: Call<ResData>, t: Throwable) {

            }

            override fun onResponse(call: Call<ResData>, response: Response<ResData>) {
                Log.d(TAG, "Status Code ${response.code()}  URL : ${call.request().url()}")
                if (response.isSuccessful) {
                    val resData = response.body()
                    if (resData != null) {
                        val rates = UtilsManager.getRateData(resData.rates.toString())
                        mAdapter.updateRates(rates)
                    }
                }
            }
        })

    }

    fun runTimer() {
        val handler = Handler()
        val timer = Timer()
        val doAsynchronousTask = object : TimerTask() {
            override fun run() {
                handler.post(Runnable {
                    try {
                        loadRates()
                    } catch (e: Exception) {
                    }
                })
            }
        }
        timer.schedule(doAsynchronousTask, 0, 1000) //execute in every 5 seconds
    }


}
