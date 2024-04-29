package com.njha.inboxapp.connection.db.cassandra;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@ConfigurationProperties(prefix = "datastax.astra")
@Setter
@Getter
public class DataStaxAstraProperties {

    private File secureConnectBundle;
}
