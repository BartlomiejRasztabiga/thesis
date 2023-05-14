package me.rasztabiga.thesis.restaurant.config

import com.thoughtworks.xstream.XStream
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class XStreamConfig {
    @Bean
    fun xStream(): XStream {
        val xStream = XStream()
        xStream.allowTypesByWildcard(arrayOf("me.rasztabiga.thesis.**"))
        return xStream
    }
}
