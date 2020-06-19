package com.doan.timnhatro.utils;

import android.content.Context;

import com.doan.timnhatro.base.BaseApplication;
import com.doan.timnhatro.base.Constants;
import com.doan.timnhatro.model.Account;
import com.google.gson.Gson;

public class AccountUtils {

    private static AccountUtils accountUtils;
    private Account account;

    public static AccountUtils getInstance(){
        if (accountUtils == null){
            accountUtils = new AccountUtils();
        }
        return accountUtils;
    }

    public void setAccount(Account account){
        BaseApplication.getContext()
                .getSharedPreferences(Constants.ACCOUNT, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.ACCOUNT,account.toString())
                .apply();

        this.account = null;
    }

    public Account getAccount(){
        if (account == null){
            String accountJson = BaseApplication.getContext()
                                .getSharedPreferences(Constants.ACCOUNT, Context.MODE_PRIVATE)
                                .getString(Constants.ACCOUNT,null);

            if (accountJson == null){
                return null;
            }

            account = new Gson().fromJson(accountJson,Account.class);
        }
        return account;
    }

    public void logOut(){
        account = null;
        BaseApplication.getContext()
                .getSharedPreferences(Constants.ACCOUNT, Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();
    }
}
