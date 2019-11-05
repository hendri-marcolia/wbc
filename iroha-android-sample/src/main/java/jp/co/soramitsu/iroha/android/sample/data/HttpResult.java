package jp.co.soramitsu.iroha.android.sample.data;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

public class HttpResult {

    @Setter
    @Getter
    @SerializedName("http_result")
    private String httpResult;

    @Setter
    @Getter
    @SerializedName("message")
    private String message;

    public HttpResult() {}
}
