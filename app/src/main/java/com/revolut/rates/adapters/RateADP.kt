package com.revolut.rates.adapters

import android.os.Handler
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.revolut.rates.R
import com.revolut.rates.RateApplication
import com.revolut.rates.interfaces.keyboardListener
import com.revolut.rates.models.RateMDL
import com.revolut.rates.settings.PrefManager
import com.revolut.rates.settings.UtilsManager
import kotlinx.android.synthetic.main.row_item.view.*
import java.util.*


class RateADP(rates: ArrayList<RateMDL>) : RecyclerView.Adapter<RateADP.ViewHolder>() {

    private var rates = mutableListOf<RateMDL>()
    private var prefs: PrefManager? = null
    private var mRecyclerView : RecyclerView? = null
    private var keyboardListener : keyboardListener? = null;

    companion object {
        private val TAG = RateADP::class.java.simpleName
    }

    init {
        this.rates = rates
        this.prefs = PrefManager(RateApplication.getContext())

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(position)
    }

    override fun getItemCount(): Int {
        return rates.size
    }

    fun getItem(position: Int): RateMDL? {
        return if (this.rates == null || this.rates[position] == null) {
            null
        } else this.rates[position]
    }

    fun addRecycler(recyclerView: RecyclerView, keyboardListener: keyboardListener){
        this.mRecyclerView = recyclerView;
        this.keyboardListener = keyboardListener;
    }

    fun updateRates(new_rates: HashMap<String, Any>) {
        val handler = Handler()
         handler.post({
             for(i in rates){
                 try {
                     i.rates = new_rates.get(i.code) as Double
                 } catch (e: Exception) {
                 }
             }
             notifyDataSetChanged()
         })

    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var editText: TextInputEditText? = null
        var cardView: CardView? = null
        var textListener: TextInputChangeListener? = null

        init {
            view.setOnClickListener(this)
            editText = itemView.findViewById(R.id.mAmount) as TextInputEditText
            cardView = itemView.findViewById(R.id.cardView) as CardView
            textListener = TextInputChangeListener()
        }

        override fun onClick(p0: View?) {
            swap()
        }

        fun onBindView(position: Int) {

            val rateMDL = getItem(position)
            if (rateMDL != null) {
                itemView.mCode.text = rateMDL.code
                itemView.mCurrency.text = rateMDL.name.capitalizeWords()

                var image_name = rateMDL.code.toLowerCase() + ".png"
                if (TextUtils.equals(image_name, "try.png")) {
                    image_name = "tr_y.png"
                }
                itemView.mImage.setImageDrawable(UtilsManager.getImage(image_name))


                try {
                    val amount = prefs!!.getAmount().toDouble()

                    if (position != 0) {
                        val amount_rate = rateMDL.rates * amount
                        editText!!.text = Editable.Factory.getInstance().newEditable(UtilsManager.formatCurrency(amount_rate))
                        rateMDL.changeValue = amount_rate.toString()
                    }else{
                        editText!!.addTextChangedListener(textListener)
                        editText!!.text = Editable.Factory.getInstance().newEditable(UtilsManager.formatCurrency(amount))
                    }

                } catch (e: NumberFormatException) {
                    Log.e(TAG, "NumberFormatException ${e.message}")
                    editText!!.text = Editable.Factory.getInstance().newEditable("0")

                } catch (ex: Exception) {
                   Log.e(TAG, "Exception 11 ${ex.message}")
                }

            }

            if(adapterPosition == 0){
                editText!!.requestFocus()
                editText!!.setSelection(editText!!.length())
            }

        }

        fun swap() {
            try {
                Collections.swap(rates, adapterPosition, 0)
                notifyItemMoved(adapterPosition, 0)
                mRecyclerView!!.scrollToPosition(0)

                prefs!!.setAmount(getItem(0)!!.changeValue)

                keyboardListener!!.onSendData(editText)

            } catch (e: Exception) {
            }
        }

        fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")

    }

    inner class TextInputChangeListener : TextWatcher {

        override fun afterTextChanged(p0: Editable?) {
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            //Log.d(TAG, "Text ==> ${p0.toString()}")
            val number = p0.toString().replace(",", ".")
            prefs!!.setAmount(number)

            try {
                notifyDataSetChanged()
            } catch (e: Exception) {
            }
        }
    }
}