package com.example.tyle.ido;

import android.app.Activity;

public class Session
{
    public static Session singletonObject;
    /** A private Constructor prevents any other class from instantiating. */

    private LoginActivity loginActivity;

    public Session()
    {
        //   Optional Code
    }
    public static synchronized Session getSingletonObject()
    {
        if (singletonObject == null)
        {
            singletonObject = new Session();
        }
        return singletonObject;
    }


    /**
     * used to clear CommonModelClass(SingletonClass) Memory
     */
    public void clear()
    {
        singletonObject = null;
    }


    public Object clone() throws CloneNotSupportedException
    {
        throw new CloneNotSupportedException();
    }

    //getters and setters starts from here.it is used to set and get a value

    public LoginActivity getLoginActivity()
    {
        return loginActivity;
    }

    public void setLoginActivity(LoginActivity loginActivity)
    {
        this.loginActivity = loginActivity;
    }

}
