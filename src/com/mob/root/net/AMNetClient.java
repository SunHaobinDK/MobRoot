package com.mob.root.net;

import org.apache.http.HttpEntity;
import android.content.Context;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

class AMNetClient {
	
	private static AsyncHttpClient client = new AsyncHttpClient();
	
    static void post(Context context, String url, HttpEntity entity, String contentType, ResponseHandlerInterface responseHandler) {
    	client.post(context, url, entity, contentType, responseHandler);
    }
    
    static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }
}
