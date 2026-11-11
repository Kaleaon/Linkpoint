package okhttp3;

import java.io.IOException;
/* loaded from: classes.dex */
public interface Interceptor {

    /* loaded from: classes.dex */
    public interface Chain {
        Connection connection();

        Response proceed(Request request) throws IOException;

        Request request();
    }

    Response intercept(Chain chain) throws IOException;
}
