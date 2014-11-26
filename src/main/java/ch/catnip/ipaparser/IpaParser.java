package ch.catnip.ipaparser;

import com.dd.plist.NSArray;
import com.dd.plist.NSDictionary;
import com.dd.plist.NSObject;
import com.dd.plist.PropertyListParser;
import net.lingala.zip4j.core.ZipFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class IpaParser {

    Logger log = LoggerFactory.getLogger(IpaParser.class);

    public Ipa parse(File ipaFile) {

        log.debug("Parsing {}", ipaFile);

        try {
            Path tmpPath = Files.createTempDirectory("ipaparser");

            ZipFile zipFile = new ZipFile(ipaFile.getAbsoluteFile());

            zipFile.extractAll(tmpPath.toString());

            File payloadFolder = new File(tmpPath + File.separator + "Payload");

            File appFolder = payloadFolder.listFiles()[0];

            log.debug("App Folder: {}", appFolder);

            File infoPlist = new File(appFolder + File.separator + "Info.plist");

            NSDictionary rootDict = (NSDictionary) PropertyListParser.parse(infoPlist);

            Ipa ipa = new Ipa();
            ipa.setBundleName(rootDict.objectForKey("CFBundleName").toString());

            if (rootDict.objectForKey("CFBundleDisplayName") != null) {
                ipa.setBundleDisplayName(rootDict.objectForKey("CFBundleDisplayName").toString());
            }
            else {
                ipa.setBundleDisplayName(ipa.getBundleName());
            }

            ipa.setBundleIdentifier(rootDict.objectForKey("CFBundleIdentifier").toString());
            ipa.setBundleShortVersionString(rootDict.objectForKey("CFBundleShortVersionString").toString());

            File iconFile;

            if (rootDict.objectForKey("CFBundleIconFiles") != null) {
                NSObject[] iconFiles = ((NSArray) rootDict.objectForKey("CFBundleIconFiles")).getArray();
                iconFile = new File(appFolder + File.separator + iconFiles[0].toString());
            }
            else {
                iconFile = new File(appFolder + File.separator + "iTunesArtwork");
            }

            BufferedImage iconImage = ImageIO.read(iconFile);

            ipa.setIcon(iconImage);

            tmpPath.toFile().delete();

            log.debug("Returning {}", ipa);

            return ipa;

        } catch (Exception e) {
            log.error("Error: ", e);
        }

        return null;
    }
}
