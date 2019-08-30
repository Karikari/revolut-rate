package com.revolut.rates.adapters

import android.content.Context
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import com.revolut.rates.R
import com.revolut.rates.models.RateMDL
import com.revolut.rates.settings.PrefManager
import com.revolut.rates.settings.UtilsManager
import kotlinx.android.synthetic.main.row_item.view.*
import java.util.*
import android.view.View.OnFocusChangeListener
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText


class RateADP(ctx: Context, rates: List<RateMDL>, recyclerView: RecyclerView) :
    RecyclerView.Adapter<RateADP.ViewHolder>() {


    private var rates = listOf<RateMDL>()
    private var context: Context
    private var recyclerView: RecyclerView
    private var prefs : PrefManager? = null

    companion object {
        private val TAG = RateADP::class.java.simpleName
    }

    init {
        this.context = ctx
        this.rates = rates
        this.recyclerView = recyclerView
        this.prefs = PrefManager(context)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.row_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.setIsRecyclable(false)
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



    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var editText: TextInputEditText? = null
        var cardView : CardView? = null

        init {
            context.let {
                view.setOnClickListener(this)
                editText = itemView.findViewById(R.id.mAmount) as TextInputEditText
                cardView = itemView.findViewById(R.id.cardView) as CardView
            }

        }

        override fun onClick(p0: View?) {
           swap()

        }

        fun onBindView(position: Int) {

            val rateMDL = getItem(position)
            if (rateMDL != null) {
                Log.d(TAG, "Position ${position}")
                itemView.mCode.text = rateMDL.code
                itemView.mCurrency.text = rateMDL.name.capitalizeWords()

                var image_name = rateMDL.code.toLowerCase()+".png"
                if(TextUtils.equals(image_name, "try.png")){
                    image_name = "tr_y.png"
                }
                itemView.mImage.setImageDrawable(UtilsManager.getImage(image_name))

                try {
                    var amount = prefs!!.getAmount().toDouble()

                    if(adapterPosition!=0){
                        amount = rateMDL.rates * amount
                        editText!!.text = Editable.Factory.getInstance().newEditable(UtilsManager.formatCurrency(amount))
                    }else{
                        editText!!.text = Editable.Factory.getInstance().newEditable(UtilsManager.formatCurrency(amount))
                    }
                } catch (e: NumberFormatException) {
                    Log.e(TAG, "Exception ${e.message}")
                } catch (ex: Exception){
                    Log.e(TAG, "Exception ${ex.message}")
                }


                editText!!.addTextChangedListener(object : TextWatcher {

                    override fun afterTextChanged(p0: Editable?) {
                    }

                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        Log.d(TAG, "Text ==> ${p0.toString()}")
                        val number = p0.toString().replace(",", ".")
                        prefs!!.setAmount(number)

                        notifyDataSetChanged()
                    }
                })


                editText!!.setOnFocusChangeListener(OnFocusChangeListener { v, hasFocus ->
                   Log.d(TAG, "HAS FOCUS $hasFocus Position $adapterPosition  ${editText!!.length()}")
                    editText!!.setSelection(editText!!.length())
                })

                if(adapterPosition!=0){
                    cardView!!.setOnClickListener {
                        swap()
                    }
                }

            }

        }

        fun swap(){
            Collections.swap(rates, adapterPosition, 0)
            notifyItemMoved(adapterPosition, 0)
            recyclerView.scrollToPosition(0)
            editText!!.requestFocus()

            context.let {
                val imm = it.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
                imm!!.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
            }
        }


        fun String.capitalizeWords(): String = split(" ").map { it.capitalize() }.joinToString(" ")


    }
}