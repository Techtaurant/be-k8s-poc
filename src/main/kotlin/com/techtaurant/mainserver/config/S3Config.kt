package com.techtaurant.mainserver.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
class S3Config(
    @Value("\${aws.s3.region}")
    private val region: String,
    @Value("\${aws.s3.endpoint:}")
    private val endpoint: String,
    @Value("\${aws.s3.presign-endpoint:}")
    private val presignEndpoint: String,
    @Value("\${aws.s3.path-style-access:false}")
    private val pathStyleAccess: Boolean,
) {
    @Bean
    fun s3Client(): S3Client {
        val builder =
            S3Client.builder()
                .region(Region.of(region))
                .serviceConfiguration(s3Configuration())

        endpoint.toUriOrNull()?.let { builder.endpointOverride(it) }
        return builder.build()
    }

    @Bean
    fun s3Presigner(): S3Presigner {
        val builder =
            S3Presigner.builder()
                .region(Region.of(region))
                .serviceConfiguration(s3Configuration())

        (presignEndpoint.toUriOrNull() ?: endpoint.toUriOrNull())
            ?.let { builder.endpointOverride(it) }

        return builder.build()
    }

    private fun s3Configuration(): S3Configuration =
        S3Configuration.builder()
            .pathStyleAccessEnabled(pathStyleAccess)
            .build()

    private fun String.toUriOrNull(): URI? = trim().takeIf { it.isNotEmpty() }?.let(URI::create)
}
