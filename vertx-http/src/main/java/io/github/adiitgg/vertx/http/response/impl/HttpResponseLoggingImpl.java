package io.github.adiitgg.vertx.http.response.impl;

import io.github.adiitgg.vertx.http.model.RoutingData;
import io.github.adiitgg.vertx.http.response.HttpResponseLogging;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.ext.web.RoutingContext;
import lombok.val;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.regex.Pattern;

public final class HttpResponseLoggingImpl implements HttpResponseLogging {

  private final Logger log = LoggerFactory.getLogger(HttpResponseLogging.class);
  private final Pattern PATTERN_CONTENT_TYPE = Pattern.compile("json|text|url");

  @Override
  public void log(RoutingContext context, Object data) {
    OffsetDateTime startTime = context.get(RoutingData.REQUEST_START_OFFSETDATETIME, OffsetDateTime.now());
    val request = context.request();
    val contentType = request.getHeader(HttpHeaders.CONTENT_TYPE);
    val statusCode = context.response().getStatusCode();
    val builder = new StringBuilder("|");
    builder.append(statusCode).append("|");
    if (startTime != null) {
      val took = calcTook(startTime);
      builder.append(leftPad(took, 8)).append(" | ");
    } else {
      builder.append("| ");
    }
    builder.append(leftPad(bytesToKilobytes(context.response().bytesWritten()) + "kb", 8)).append(" | ");
    builder.append(leftPad(getClientIpPort(context.request()), 15)).append(" - ");
    val requestNoLog = context.get(RoutingData.REQUEST_NO_LOG, false);
    val responseNoLog = context.get(RoutingData.RESPONSE_NO_LOG, false);
    val isReqBodyLoggable = !requestNoLog && request.method() != HttpMethod.GET && !context.body().isEmpty() && contentType != null && PATTERN_CONTENT_TYPE.matcher(contentType).find();
    builder.append(request.method().name())
      .append(" ").append(request.uri())
      .append(isReqBodyLoggable ? (" - req=" + context.body().asString().replace("\r", "\\r").replace("\n", "\\n") + (responseNoLog ? "" : ";")) : "");

    if (!responseNoLog && data != null) {
      if (!isReqBodyLoggable) {
        builder.append(" - ");
      }
      builder.append("resBody=").append(data);
    }
    log.info(builder.toString());
  }

  private String getClientIpPort(HttpServerRequest request) {
    String xForwardedFor = request.getHeader("X-Forwarded-For");
    if (null == xForwardedFor) {
      return request.remoteAddress().toString();
    }
    return xForwardedFor.split(",")[0] + ":" + request.remoteAddress().port();
  }

  private String leftPad(String originalString, int length) {
    char padChar = ' ';
    if (originalString == null) {
      return null;
    }
    int padLength = length - originalString.length();
    if (padLength <= 0) {
      return originalString;
    }
    return String.valueOf(padChar).repeat(padLength) + originalString;
  }

  private String calcTook(OffsetDateTime startTime) {
    // Calculate the duration between the start time and the current time
    Duration duration = Duration.between(startTime, OffsetDateTime.now());
    // Convert the duration to minutes, seconds, milliseconds, and nanoseconds
    long minutes = duration.toMinutes();
    long seconds = duration.toMillis() / 1000;
    long milliseconds = duration.toMillis();
    long nanoseconds = duration.toNanos();
    // Initialize the "took" variable
    String took = "";
    // Determine the appropriate unit for the duration and assign it to the "took" variable
    if (minutes > 0) {
      took = minutes + "m";
    } else if (seconds > 0) {
      took = seconds + "s";
    } else if (milliseconds > 0) {
      took = milliseconds + "ms";
    } else {
      took = nanoseconds + "ns";
    }
    return took;
  }

  private String bytesToKilobytes(long bytes) {
    String result = (bytes / 1024.0) + "";
    if (result.length() > 6) {
      return result.substring(0, 6);
    }
    return result;
  }

}
