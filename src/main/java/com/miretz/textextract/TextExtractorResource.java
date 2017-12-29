package com.miretz.textextract;

import com.miretz.textextract.dto.Extraction;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.glassfish.grizzly.streams.Input;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.ws.rs.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("extractor")
public class TextExtractorResource {

    private final static Logger LOGGER = Logger.getLogger(TextExtractorResource.class.getName());

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response extractDocument(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        try {
            final Extraction extraction = parseDocument(uploadedInputStream);
            return Response.ok(extraction).build();
        } catch (IOException | SAXException | TikaException e) {
            LOGGER.log(Level.SEVERE, "an exception was thrown", e);
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        }
    }


    private Extraction parseDocument(InputStream stream) throws IOException, SAXException, TikaException {
        final BodyContentHandler handler = new BodyContentHandler();
        final AutoDetectParser parser = new AutoDetectParser();
        final Metadata metadata = new Metadata();

        parser.parse(stream, handler, metadata);

        final String plainText = handler.toString();
        final String language = detectLanguage(plainText);

        final Map<String, String> extractedMetadata = new HashMap<>();
        Arrays.stream(metadata.names()).forEach(name -> {
            extractedMetadata.put(name, metadata.get(name));
        });

        return new Extraction(plainText, language, extractedMetadata);
    }

    private String detectLanguage(String text) throws IOException {
        LanguageDetector detector = new OptimaizeLangDetector().loadModels();
        LanguageResult result = detector.detect(text);
        return result.getLanguage();
    }


}
