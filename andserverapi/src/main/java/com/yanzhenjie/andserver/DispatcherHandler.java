/*
 * Copyright 2018 Yan Zhenjie.
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
package com.yanzhenjie.andserver;

import android.content.Context;

import android.util.Log;

import com.yanzhenjie.andserver.error.NotFoundException;
import com.yanzhenjie.andserver.error.ServerInternalException;
import com.yanzhenjie.andserver.framework.ExceptionResolver;
import com.yanzhenjie.andserver.framework.HandlerInterceptor;
import com.yanzhenjie.andserver.framework.MessageConverter;
import com.yanzhenjie.andserver.framework.ModifiedInterceptor;
import com.yanzhenjie.andserver.framework.body.StringBody;
import com.yanzhenjie.andserver.framework.handler.HandlerAdapter;
import com.yanzhenjie.andserver.framework.handler.RequestHandler;
import com.yanzhenjie.andserver.framework.view.View;
import com.yanzhenjie.andserver.framework.view.ViewResolver;
import com.yanzhenjie.andserver.http.HttpContext;
import com.yanzhenjie.andserver.http.HttpRequest;
import com.yanzhenjie.andserver.http.HttpResponse;
import com.yanzhenjie.andserver.http.RequestDispatcher;
import com.yanzhenjie.andserver.http.RequestWrapper;
import com.yanzhenjie.andserver.http.StandardContext;
import com.yanzhenjie.andserver.http.StandardRequest;
import com.yanzhenjie.andserver.http.StandardResponse;
import com.yanzhenjie.andserver.http.cookie.Cookie;
import com.yanzhenjie.andserver.http.multipart.MultipartRequest;
import com.yanzhenjie.andserver.http.multipart.MultipartResolver;
import com.yanzhenjie.andserver.http.multipart.StandardMultipartResolver;
import com.yanzhenjie.andserver.http.session.Session;
import com.yanzhenjie.andserver.http.session.SessionManager;
import com.yanzhenjie.andserver.http.session.StandardSessionManager;
import com.yanzhenjie.andserver.register.Register;
import com.yanzhenjie.andserver.util.Assert;
import com.yanzhenjie.andserver.util.StatusCode;

import org.apache.httpcore.protocol.HttpRequestHandler;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by YanZhenjie on 2018/8/8.
 */
public class DispatcherHandler implements HttpRequestHandler, Register {

    private SessionManager mSessionManager;
    private MultipartResolver mMultipartResolver;
    private MessageConverter mConverter;
    private ViewResolver mViewResolver;
    private ExceptionResolver mResolver;

    private List<HandlerAdapter> mAdapterList = new LinkedList<>();
    private List<HandlerInterceptor> mInterceptorList = new LinkedList<>();

    public DispatcherHandler(Context context) {
        this.mSessionManager = new StandardSessionManager(context);
        this.mMultipartResolver = new StandardMultipartResolver(context);
        this.mViewResolver = new ViewResolver();
        this.mResolver = ExceptionResolver.DEFAULT;

        this.mInterceptorList.add(new ModifiedInterceptor());
    }

    @Override
    public void addAdapter( HandlerAdapter adapter) {
        Assert.notNull(adapter, "The adapter cannot be null.");

        if (!mAdapterList.contains(adapter)) {
            mAdapterList.add(adapter);
        }
    }

    @Override
    public void addInterceptor( HandlerInterceptor interceptor) {
        Assert.notNull(interceptor, "The interceptor cannot be null.");

        if (!mInterceptorList.contains(interceptor)) {
            mInterceptorList.add(interceptor);
        }
    }

    @Override
    public void setConverter(MessageConverter converter) {
        this.mConverter = converter;
        this.mViewResolver = new ViewResolver(converter);
    }

    @Override
    public void setResolver( ExceptionResolver resolver) {
        Assert.notNull(resolver, "The exceptionResolver cannot be null.");

        this.mResolver = resolver;
    }

    @Override
    public void handle(org.apache.httpcore.HttpRequest req, org.apache.httpcore.HttpResponse res,
        org.apache.httpcore.protocol.HttpContext con) {
        HttpRequest request = new StandardRequest(req, new StandardContext(con), this, mSessionManager);
        HttpResponse response = new StandardResponse(res);
        handle(request, response);
    }

    private void handle(HttpRequest request, HttpResponse response) {
        try {
            if (mMultipartResolver.isMultipart(request)) {
                request = mMultipartResolver.resolveMultipart(request);
            }

            // Determine adapter for the current request.
            HandlerAdapter ha = getHandlerAdapter(request);
            if (ha == null) throw new NotFoundException(request.getPath());

            // Determine handler for the current request.
            RequestHandler handler = ha.getHandler(request);
            if (handler == null) throw new NotFoundException(request.getPath());

            // Pre processor, e.g. interceptor.
            if (preHandle(request, response, handler)) return;

            // Actually invoke the handler.
            request.setAttribute(HttpContext.HTTP_MESSAGE_CONVERTER, mConverter);
            View view = handler.handle(request, response);
            mViewResolver.resolve(view, request, response);
            processSession(request, response);
        } catch (Throwable err) {
            try {
                mResolver.onResolve(request, response, err);
            } catch (Exception e) {
                e = new ServerInternalException(e);
                response.setStatus(StatusCode.SC_INTERNAL_SERVER_ERROR);
                response.setBody(new StringBody(e.getMessage()));
            }
            processSession(request, response);
        } finally {
            if (request instanceof MultipartRequest) {
                mMultipartResolver.cleanupMultipart((MultipartRequest)request);
            }
        }
    }

    /**
     * Return the {@link RequestHandler} for this request.
     *
     * @param request current HTTP request.
     *
     * @return the {@link RequestHandler}, or {@code null} if no handler could be found.
     */
    private HandlerAdapter getHandlerAdapter(HttpRequest request) {
        for (HandlerAdapter ha : mAdapterList) {
            if (ha.intercept(request)) return ha;
        }
        return null;
    }

    /**
     * Intercept the execution of a handler.
     *
     * @param request current request.
     * @param response current response.
     * @param handler the corresponding handler of the current request.
     *
     * @return true if the interceptor has processed the request and responded.
     */
    private boolean preHandle(HttpRequest request, HttpResponse response, RequestHandler handler) throws Exception {
        for (HandlerInterceptor interceptor : mInterceptorList) {
            if (interceptor.onIntercept(request, response, handler)) return true;
        }
        return false;
    }

    public RequestDispatcher getRequestDispatcher(final HttpRequest request, final String path) {
        HttpRequest copyRequest = request;
        while (copyRequest instanceof RequestWrapper) {
            RequestWrapper wrapper = (RequestWrapper)request;
            copyRequest = wrapper.getRequest();
        }

        StandardRequest newRequest = (StandardRequest)copyRequest;
        newRequest.setPath(path);

        HandlerAdapter ha = getHandlerAdapter(copyRequest);
        if (ha == null) {
            throw new NotFoundException(request.getPath());
        }

        return new RequestDispatcher() {
            @Override
            public void forward( HttpRequest request,  HttpResponse response) {
                handle(request, response);
            }
        };
    }

    private void processSession(HttpRequest request, HttpResponse response) {
        Object objSession = request.getAttribute(HttpContext.REQUEST_CREATED_SESSION);
        if (objSession != null && objSession instanceof Session) {
            Session session = (Session)objSession;
            try {
                mSessionManager.add(session);
            } catch (IOException e) {
                Log.e(AndServer.TAG, "Session persistence failed.", e);
            }

            Cookie cookie = new Cookie(HttpRequest.SESSION_NAME, session.getId());
            cookie.setPath("/");
            cookie.setHttpOnly(true);
            response.addCookie(cookie);
        }
    }
}