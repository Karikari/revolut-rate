package com.revolut.rates.fragments


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.revolut.rates.R
import com.revolut.rates.RateApplication
import com.revolut.rates.adapters.RateADP
import com.revolut.rates.http.RateHttp
import com.revolut.rates.http.models.Country
import com.revolut.rates.http.models.ResData
import com.revolut.rates.models.RateMDL
import com.revolut.rates.settings.PrefManager
import com.revolut.rates.settings.UtilsManager
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 */
class HomeFragment : Fragment() {

    companion object {
        private val TAG = HomeFragment::class.java.simpleName
    }

    private val req : RateHttp.IRequest by lazy {
        RateHttp.getInstance()
    }

    private val prefs : PrefManager by lazy {
        PrefManager(RateApplication.getContext())
    }

    var countries = hashMapOf<String, Country>()
    var rates = hashMapOf<String, Any>()
    var records = mutableListOf<RateMDL>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        countries = UtilsManager.getCountryData(activity)

        prefs.setAmount("100")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mSwipe.isRefreshing = true
        loadData()

        mSwipe.setOnRefreshListener {
            records.clear()
            rates.clear()
            loadData()
        }

    }

    fun loadData(){
        req.rates.enqueue(object : Callback<ResData> {

            override fun onFailure(call: Call<ResData>, t: Throwable) {
                mSwipe.isRefreshing = false
                Log.e(TAG, "Request Errro ${t.message}")
                Snackbar.make(mSwipe, "${t.message}", Snackbar.LENGTH_LONG).show()
            }

            override fun onResponse(call: Call<ResData>, response: Response<ResData>) {
                Log.d(TAG, "Status Code ${response.code()}  URL : ${call.request().url()}")
                if(response.isSuccessful){
                    val resData = response.body()
                    if(resData!=null){
                        rates = UtilsManager.getRateData(resData.rates.toString())
                    }

                    val record = RateMDL()
                    record.name = "EURO"
                    record.code = "EUR"
                    record.symbol = ""
                    record.rates = 1.0

                    records.add(record)
                    for ((key, value) in rates){
                        val country = countries.get(key)
                        if(country!=null){
                            val record = RateMDL()
                            record.name = country.name
                            record.code = key
                            record.symbol = country.symbol
                            record.rates = value as Double
                            records.add(record)
                        }
                    }
                    Log.d(TAG, "Records  ==> ${records.size}")
                    listRates(records)
                    mSwipe.isRefreshing = false
                }
            }
        })
    }


    fun listRates(rates : List<RateMDL>){
        mRecyclerView.hasFixedSize()
        mRecyclerView.layoutManager = LinearLayoutManager(activity)
        val mAdapter = context?.let { RateADP(it, rates, mRecyclerView) }
        mRecyclerView.adapter = mAdapter
        mAdapter!!.notifyDataSetChanged()
    }


}
