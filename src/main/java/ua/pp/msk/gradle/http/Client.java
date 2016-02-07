/*
 * Copyright 2016 Maksym Shkolnyi self project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ua.pp.msk.gradle.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.protocol.ClientContextConfigurer;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.TargetAuthenticationStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.pp.msk.gradle.exceptions.BadCredentialsException;
import ua.pp.msk.gradle.exceptions.ClientSslException;
import ua.pp.msk.gradle.exceptions.ResponseException;
import ua.pp.msk.gradle.ext.NexusConf;

/**
 *
 * @author Maksym Shkolnyi aka Maksym Shkolnyi <mshkolnyi@ukr.net> aka maskimko
 */
public class Client {

    private URL targetUrl;
    private final Logger logger = LoggerFactory.getLogger(Client.class);
    private HttpContext context;
    private HttpClient client;
    private String userAgent = "Maven Dependency pushing plugin";

    public Client(String url, String user, String password) throws ClientSslException, MalformedURLException {
        if (!url.contains("/nexus/service/local/artifact/maven/content")) {
            url = url.concat("/nexus/service/local/artifact/maven/content");
        }
        URL targetURL = new URL(url);
        init(targetURL, user, password);
    }

    private void init(URL targetURL, String user, String password) throws ClientSslException {
        this.targetUrl = targetURL;
        logger.debug("Initializing " + this.getClass().getName() + " with target URL " + targetURL.toString());
        HttpHost htHost = new HttpHost(targetUrl.getHost(), targetUrl.getPort(), targetUrl.getProtocol());

        AuthCache aCache = new BasicAuthCache();
        BasicScheme basicAuth = new BasicScheme();
        aCache.put(htHost, basicAuth);

        UsernamePasswordCredentials creds = new UsernamePasswordCredentials(user, password);
        BasicCredentialsProvider cProvider = new BasicCredentialsProvider();
        cProvider.setCredentials(new AuthScope(htHost), creds);
        logger.debug("Credential provider: " + cProvider.toString());

        context = new BasicHttpContext();
        ClientContextConfigurer cliCon = new ClientContextConfigurer(context);
        cliCon.setCredentialsProvider(cProvider);
        context.setAttribute(ClientContext.AUTH_CACHE, aCache);
        SSLSocketFactory sslConnectionSocketFactory = null;
        try {
            sslConnectionSocketFactory = new SSLSocketFactory(new TrustSelfSignedStrategy(), new NexusHostnameVerifier());
        } catch (KeyManagementException ex) {
            logger.error("Cannot manage secure keys", ex);
            throw new ClientSslException("Cannot manage secure keys", ex);
        } catch (KeyStoreException ex) {
            logger.error("Cannot build SSL context due to KeyStore error", ex);
            throw new ClientSslException("Cannot build SSL context due to KeyStore error", ex);
        } catch (NoSuchAlgorithmException ex) {
            logger.error("Unsupported security algorithm", ex);
            throw new ClientSslException("Unsupported security algorithm", ex);
        } catch (UnrecoverableKeyException ex) {
            logger.error("Unrecoverable key", ex);
            throw new ClientSslException("Unrecoverrable key", ex);
        }

        DefaultHttpClient defClient = new DefaultHttpClient();
        defClient.setRedirectStrategy(new NexusRedirectStrategy());
        defClient.setCredentialsProvider(cProvider);
        Scheme https = new Scheme("https", 443, sslConnectionSocketFactory);
        defClient.getConnectionManager().getSchemeRegistry().register(https);
        defClient.setTargetAuthenticationStrategy(new TargetAuthenticationStrategy());
        client = defClient;
    }

    public boolean upload(NexusConf nc) {
        boolean result = false;
        try {
            HttpPost httpPost = new HttpPost(targetUrl.toString());
            //httpPost.setHeader("Content-Type", "multipart/form-data");

            MultipartEntity me = new MultipartEntity(HttpMultipartMode.STRICT);
//            FormBodyPart fbp = new FormBodyPart("form", new StringBody("check it"));
//            fbp.addField("r", nc.getRepository());
//            fbp.addField("hasPom", "" + nc.isHasPom());
//            fbp.addField("e", nc.getExtension());
//            fbp.addField("g", nc.getGroup());
//            fbp.addField("a", nc.getArtifact());
//            fbp.addField("v", nc.getVersion());
//            fbp.addField("p", nc.getPackaging());
//            me.addPart(fbp);
            File rpmFile = new File(nc.getFile());
            ContentBody cb = new FileBody(rpmFile);
            me.addPart("p", new StringBody(nc.getPackaging()));
            me.addPart("e", new StringBody(nc.getExtension()));
            me.addPart("r", new StringBody(nc.getRepository()));
            me.addPart("g", new StringBody(nc.getGroup()));
            me.addPart("a", new StringBody(nc.getArtifact()));
            me.addPart("v", new StringBody(nc.getVersion()));
            me.addPart("file", cb);

            httpPost.setHeader("User-Agent", userAgent);
            httpPost.setEntity(me);

            logger.debug("Sending request");
            HttpResponse postResponse = client.execute(httpPost, context);
            logger.debug("Status line: " + postResponse.getStatusLine().toString());
            int statusCode = postResponse.getStatusLine().getStatusCode();

            HttpEntity entity = postResponse.getEntity();

            try (BufferedReader bufReader = new BufferedReader(new InputStreamReader(entity.getContent()))) {
                bufReader.lines().forEach(e -> logger.debug(e));
            } catch (IOException ex) {
                logger.warn("Cannot get entity response");

            }

            switch (statusCode) {
                case 200:
                    logger.debug("Got a successful http response " + postResponse.getStatusLine());
                    result = true;
                    break;
                case 201:
                    logger.debug("Created! Got a successful http response " + postResponse.getStatusLine());
                    result = true;
                    break;
                case 401:
                    throw new BadCredentialsException("Bad credentials. Response status: " + postResponse.getStatusLine());
                default:
                    throw new ResponseException("Response is not OK. Response status: " + postResponse.getStatusLine());
            }

            EntityUtils.consume(entity);

        } catch (UnsupportedEncodingException ex) {
            logger.error("Encoding is unsuported ", ex);
        } catch (IOException ex) {
            logger.error("Got IO excepption ", ex);
        } catch (ResponseException | BadCredentialsException ex) {
            logger.error("Cannot upload artifact", ex);
        }
        return result;
    }
}
