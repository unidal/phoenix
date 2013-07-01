package com.dianping.phoenix.service.netty;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.unidal.lookup.annotation.Inject;

import com.dianping.phoenix.configure.ConfigManager;
import com.dianping.phoenix.project.entity.Host;

public class DefaultAgentStatusFetcher implements AgentStatusFetcher {

	@Inject
	ConfigManager m_config;

	@Override
	public void fetchPhoenixAgentStatus(final List<Host> hosts) {
		if (hosts == null) {
			throw new IllegalArgumentException("Parameter [hosts] can not be null.");
		}

		if (hosts.size() > 0) {
			final CountDownLatch latch = new CountDownLatch(hosts.size());
			final ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(
					Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
			final Timer timer = new HashedWheelTimer();
			bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
				public ChannelPipeline getPipeline() throws Exception {
					ChannelPipeline pipeline = Channels.pipeline();
					pipeline.addFirst("timeout", new ReadTimeoutHandler(timer, 50, TimeUnit.MILLISECONDS));
					pipeline.addLast("codec", new HttpClientCodec());
					pipeline.addLast("inflater", new HttpContentDecompressor());
					pipeline.addLast("handler", new AgentStatusFetcherHandler(latch, hosts));
					return pipeline;
				}
			});
			bootstrap.setOption("connectTimeoutMillis", 50);
			bootstrap.setOption("keepAlive", true);

			for (Host host : hosts) {
				final URI uri = validateUri(m_config.getAgentStatusUrl(host.getIp()));
				if (uri == null) {
					latch.countDown();
					continue;
				}

				ChannelFuture future = bootstrap.connect(new InetSocketAddress(uri.getHost(), uri.getPort()));
				future.addListener(new ChannelFutureListener() {
					@Override
					public void operationComplete(final ChannelFuture future) throws Exception {
						if (future.isSuccess()) {
							HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, uri
									.getRawPath() == null || uri.getRawPath().length() == 0 ? "/" : uri.getRawPath());
							request.setHeader(HttpHeaders.Names.HOST, uri.getHost());
							request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
							request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);

							Channel channel = future.getChannel();
							if (channel.isConnected() && channel.isWritable() && channel.isOpen()) {
								channel.write(request);
							} else {
								channel.close();
								latch.countDown();
							}
						}
					}
				});
			}

			try {
				latch.await(3, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			bootstrap.releaseExternalResources();
		}
	}

	private URI validateUri(String agentStatusUrl) {
		String str = agentStatusUrl.trim();
		if (str != null && str.length() > 0) {
			try {
				URI uri = new URI(str);
				if (uri.getHost() != null && "http".equals(uri.getScheme()) && uri.getPort() != -1) {
					return uri;
				}
			} catch (Exception e) {
				// ignore it
			}
		}
		return null;
	}
}
