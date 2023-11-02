package me.rasztabiga.thesis.gateway

import io.netty.channel.epoll.EpollDatagramChannel
import io.netty.resolver.dns.DnsAddressResolverGroup
import io.netty.resolver.dns.DnsNameResolverBuilder
import io.netty.resolver.dns.NoopDnsCache
import org.springframework.cloud.gateway.config.HttpClientCustomizer
import org.springframework.stereotype.Component
import reactor.netty.http.client.HttpClient

@Component
class RemoveCacheCustomizer : HttpClientCustomizer {
    override fun customize(httpClient: HttpClient): HttpClient {
        val dnsResolverBuilder = DnsNameResolverBuilder()
            .channelFactory { EpollDatagramChannel() }
            .resolveCache(NoopDnsCache.INSTANCE)
        return httpClient.resolver(DnsAddressResolverGroup(dnsResolverBuilder))
    }
}
