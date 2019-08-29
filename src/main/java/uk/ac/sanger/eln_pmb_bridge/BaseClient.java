/*
 * Copyright (c) 2016 Genome Research Ltd. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.sanger.eln_pmb_bridge;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import java.io.*;
import java.net.*;

import static java.net.HttpURLConnection.HTTP_NOT_FOUND;

/**
 * Base class for a client that needs to post and get json.
 * @author dr6
 */
abstract class BaseClient {
    private final Proxy proxy;

    /**
     * Constructs a BaseClient with the given proxy
     * @param proxy proxy to use
     */
    protected BaseClient(Proxy proxy) {
        this.proxy = proxy;
    }

    protected BaseClient() {
        this(null);
    }

    /**
     * Gets the proxy
     * @return the proxy
     */
    protected Proxy getProxy() {
        return this.proxy;
    }

    protected boolean responseIsGood(int responseCode) {
        return (responseCode / 100 == 2);
    }

    /**
     * Posts some json and receives a json object response
     * @param url location to post to
     * @param data json to post
     * @return the json object received
     * @exception IOException there was a communication problem
     * @exception JSONException there was a problem parsing the response
     */
    public JSONObject postJson(URL url, Object data) throws IOException, JSONException {
        HttpURLConnection connection = openConnection(url);
        try {
            attemptPost(data, connection);
            return new JSONObject(getResponseString(connection.getInputStream()));
        } finally {
            connection.disconnect();
        }
    }

    private HttpURLConnection openConnection(URL url) throws IOException {
        Proxy proxy = getProxy();
        URLConnection con;
        if (proxy==null) {
            con = url.openConnection();
        } else {
            con = url.openConnection(proxy);
        }
        return (HttpURLConnection) con;
    }

    /**
     * Posts some json
     * @param url location to post to
     * @param data json to post
     * @exception IOException there was a communication problem
     */
    public void postJsonVoid(URL url, Object data) throws IOException {
        HttpURLConnection connection = openConnection(url);
        try {
            attemptPost(data, connection);
        } finally {
            connection.disconnect();
        }
    }

    private void attemptPost(Object data, HttpURLConnection connection) throws IOException {
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        setHeaders(connection);
        connection.connect();
        try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream())) {
            out.write(data.toString());
            out.flush();
            int responseCode = connection.getResponseCode();
            if (responseCode == HTTP_NOT_FOUND) {
                throw new Http404Exception();
            }
            // to allow for sequencescape weirdness, if shouldFollowRedirect() is false,
            //  we treat 'moved' as a success response
            if (!responseIsGood(responseCode)) {
                throw new IOException(responseCode + " - " + getResponseString(connection.getErrorStream()));
            }
        }
    }

    protected void setHeaders(HttpURLConnection connection) {
        setUsualHeaders(connection);
    }

    /**
     * Reads from an {@code InputStream} line by line, concatenates the lines and returns the result.
     * @param is stream to read
     * @return the text read from the input stream
     * @exception IOException there was a communication problem
     */
    public static String getResponseString(InputStream is) throws IOException {
        if (is==null) {
            return null;
        }
        try (BufferedReader in = new BufferedReader(new InputStreamReader(is))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }

    /**
     * Sets up the headers on a connection.
     * The headers specify JSON in and JSON out
     * @param connection the connection to set the headers on
     */
    public static void setUsualHeaders(HttpURLConnection connection) {
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Accept", "application/json");
    }

    /**
     * Custom exception indicating an HTTP 404
     * @author dr6
     */
    public static class Http404Exception extends IOException {
        public Http404Exception() {
            super(HTTP_NOT_FOUND + " - NOT FOUND");
        }
    }
}
