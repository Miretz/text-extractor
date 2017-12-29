package com.miretz.textextract;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.miretz.textextract.dto.Extraction;
import org.apache.http.HttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.glassfish.grizzly.http.server.HttpServer;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.media.multipart.file.StreamDataBodyPart;
import org.glassfish.jersey.media.multipart.internal.MultiPartWriter;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import static org.apache.commons.lang.StringUtils.isNotEmpty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class TextExtractorResourceTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        server = Main.startServer();

        final Client client = ClientBuilder.newBuilder()
                .register(MultiPartFeature.class)
                .build();
        target = client.target(Main.BASE_URI);

    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void testExtractor() {

        StreamDataBodyPart filePart = new StreamDataBodyPart("file",
                TextExtractorResource.class.getResourceAsStream("/test.pdf"),
                "test.pdf");
        MultiPart multiPart = new FormDataMultiPart().bodyPart(filePart);
        Entity entity = Entity.entity(multiPart, multiPart.getMediaType());
        Extraction extraction = target.path("extractor").request().post(entity, Extraction.class);

        assertEquals(true, isNotEmpty(extraction.getExtractedText()));
    }
}
