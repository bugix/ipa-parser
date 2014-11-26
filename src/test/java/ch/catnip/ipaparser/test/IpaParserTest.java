package ch.catnip.ipaparser.test;

import ch.catnip.ipaparser.Ipa;
import ch.catnip.ipaparser.IpaParser;
import org.junit.Test;

import java.io.File;
import java.net.URL;

import static org.junit.Assert.*;

public class IpaParserTest {

    @Test
    public void testParser() {

        URL ipaUrl = getClass().getResource("/ParserTestApp.ipa");

        File ipaFile = new File(ipaUrl.getFile());

        IpaParser parser = new IpaParser();
        Ipa ipa = parser.parse(ipaFile);

        assertEquals("BundleName", "ParserTestApp", ipa.getBundleDisplayName());
        assertEquals("ImageDimensions", 512, ipa.getIcon().getHeight());
    }
}
