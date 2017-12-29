package com.miretz.textextract;

import com.miretz.textextract.dto.Extraction;
import org.apache.tika.exception.TikaException;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageDetector;
import org.apache.tika.language.detect.LanguageResult;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.ToXMLContentHandler;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@Path("html")
public class HtmlExtractorResource {

    private final static Logger LOGGER = Logger.getLogger(HtmlExtractorResource.class.getName());

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.TEXT_HTML)
    public Response extractDocument(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail) {

        try {
            return Response.ok(parseDocument(uploadedInputStream)).build();
        } catch (IOException | SAXException | TikaException e) {
            LOGGER.log(Level.SEVERE, "an exception was thrown", e);
            return Response.serverError().entity(e.getLocalizedMessage()).build();
        }
    }


    private String parseDocument(InputStream stream) throws IOException, SAXException, TikaException {
        final ContentHandler handler = new ToXMLContentHandler();
        final AutoDetectParser parser = new AutoDetectParser();
        final Metadata metadata = new Metadata();

        parser.parse(stream, handler, metadata);

        return handler.toString();
    }


}
