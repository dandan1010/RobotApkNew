package com.example.robot.websocket;

import android.os.SystemClock;
import android.util.Log;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketUtil {
    private static final String TAG = "WebSocketUtil";

    private static Map<String, Observable<String>> observableMap;

    private static Map<String, WebSocket> webSocketMap = new WeakHashMap<>();

    static {
        observableMap = new WeakHashMap<>();
    }


    public static Observable<String> getWebSocket(final String paramString) {
        Observable<String> observable1 = observableMap.get(paramString);
        Observable<String> observable2 = observable1;
        if (observable1 == null) {
            observable2 = getWebSocketNoCache(paramString).doOnError(new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
//                    Log.d(TAG, "doOnError:" + throwable.getMessage());
                    observableMap.remove(paramString);
                }
            });
            observableMap.put(paramString, observable2);
        }
        return observable2;
    }

    public static Observable<String> getWebSocketNoCache(String paramString) {
        return Observable.create(new WebSocketOnSubscribe(paramString))
                .throttleLast(500L, TimeUnit.MILLISECONDS)
                .timeout(10L, TimeUnit.SECONDS)
                .retry().share()
                .subscribeOn(Schedulers.io());
    }


    private static final class WebSocketOnSubscribe implements ObservableOnSubscribe<String> {
        private String mUrl;
        private WebSocket mWebSocket;
        private OkHttpClient mClient;
        private Request mRequest;


        public WebSocketOnSubscribe(String paramString) {
            this.mUrl = paramString;

            mClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(5, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .build();
            mRequest = new Request.Builder().url(mUrl).build();

//            Log.d(TAG, "WebSocketOnSubscribe" + url);

        }

        @Override
        public void subscribe(final ObservableEmitter<String> emitter) throws Exception {

            if (mWebSocket != null) {
                SystemClock.sleep(2000);
//                Log.d(TAG, "mWebSocket!=null" + url);
            }
//            Log.d(TAG, "subscribe" + url);


            mWebSocket = mClient.newWebSocket(mRequest, new WebSocketListener() {
                @Override
                public void onOpen(WebSocket webSocket, Response response) {
                    super.onOpen(webSocket, response);
                    Log.d(TAG, "onOpen" + mUrl);
                    webSocketMap.put(mUrl, webSocket);
                }

                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    super.onMessage(webSocket, text);
                    if (!emitter.isDisposed()) {
                        try {
                            emitter.onNext(text);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onClosing(WebSocket webSocket, int code, String reason) {
                    super.onClosing(webSocket, code, reason);
                    webSocket.close(1000, null);

                }

                @Override
                public void onClosed(WebSocket webSocket, int code, String reason) {
                    super.onClosed(webSocket, code, reason);
                    Log.d(TAG, "onClosed" + mUrl);
                    webSocketMap.remove(mUrl);
                }

                @Override
                public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                    super.onFailure(webSocket, t, response);
                    Log.d(TAG, "onFailure" + mUrl);
                    webSocketMap.remove(mUrl);
                    if (!emitter.isDisposed()) {
                        emitter.onError(t);
                    }
                }
            });

            emitter.setDisposable(new MainThreadDisposable() {
                @Override
                protected void onDispose() {
                    Log.d(TAG, "MainThreadDisposable" + mUrl);
                    mWebSocket.close(3000, null);
                }
            });

        }
    }
}
