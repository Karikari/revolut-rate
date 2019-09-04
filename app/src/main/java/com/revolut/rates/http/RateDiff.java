package com.revolut.rates.http;

import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.revolut.rates.models.RateMDL;

import java.util.ArrayList;
import java.util.List;

public class RateDiff extends DiffUtil.Callback {

    private  List<RateMDL> ratesNew = new ArrayList<>();
    private  List<RateMDL> ratesOld = new ArrayList<>();

    public RateDiff(List<RateMDL> ratesOld, List<RateMDL> ratesNew) {
        this.ratesNew = ratesNew;
        this.ratesOld = ratesOld;
    }

    @Override
    public int getOldListSize() {
        return ratesOld.size();
    }

    @Override
    public int getNewListSize() {
        return ratesNew.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        //return ratesOld.get(oldItemPosition).equals(ratesNew.get(newItemPosition));
        //return ratesOld.get(oldItemPosition).getRates() == ratesNew.get(newItemPosition).getRates();
        return TextUtils.equals(ratesOld.get(oldItemPosition).getCode(),ratesNew.get(newItemPosition).getCode());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        RateMDL rates_old = ratesOld.get(oldItemPosition);
        RateMDL rates_new = ratesNew.get(newItemPosition);
        //return rates_new.getRates() == rates_old.getRates();
        return rates_new.equals(rates_old);
    }


    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }


}
