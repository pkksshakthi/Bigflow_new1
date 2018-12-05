package network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import presenter.VolleyCallback;

import static com.android.volley.VolleyLog.TAG;

public class CallbackHandler {
    static Context mContext;
    private static StringRequest mStringRequest;
    private static RequestQueue mRequestQueue;
    private static CallbackHandler mInstance;

    public CallbackHandler(Context ctx) {
        mContext = ctx;
        mRequestQueue = getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public static synchronized CallbackHandler getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new CallbackHandler(context);
        }
        return mInstance;
    }

    public static RequestQueue sendReqest(Context context, int method, final String requestBody, String URL, final VolleyCallback success) throws IllegalStateException {

        mContext = context;

        mRequestQueue = Volley.newRequestQueue(context, httpsSSLcheck());

        mStringRequest = new StringRequest(method, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                success.onSuccess(response);


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    success.onFailure("TimeoutError");
                } else if (error instanceof NoConnectionError) {
                    success.onFailure("NoConnectionError");
                } else if (error instanceof AuthFailureError) {
                    success.onFailure("AuthFailureError");
                } else if (error instanceof ServerError) {
                    success.onFailure("ServerError");
                } else if (error instanceof NetworkError) {
                    success.onFailure("NetworkError");
                } else if (error instanceof ParseError) {
                    success.onFailure("ParseError");
                }

            }

        }) {


            @Override
            public byte[] getBody() throws AuthFailureError {


                try {
                    return requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                return new byte[0];
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                //This appears in the log
                Log.d(TAG, "Does it assign headers?");
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                headers.put("authorization", "Token " + "7111797114100105971106449505132");

                return headers;
            }
        };

        mStringRequest.setRetryPolicy(new DefaultRetryPolicy(200 * 30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        CallbackHandler.getInstance(mContext).addToRequestQueue(mStringRequest);
        return mRequestQueue;
    }

    private static HttpStack httpsSSLcheck() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                            return myTrustedAnchors;
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    if (arg0.equalsIgnoreCase("www.vsolvengineering.com") ||
                            arg0.equalsIgnoreCase("174.138.120.196")) {
                        return true;
                    } else {
                        return false;
                    }

                }
            });
        } catch (Exception e) {
        }
        return null;
    }
}
