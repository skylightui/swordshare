package org.skylightui.swordshare.util;

import android.util.Log;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.logging.XMLFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SimpleSWORDDeposit {

    /** The XML atom entry response */
    private String xml;

    /** The debugging tag */
    private static final String TAG = "org.skylightui.swordshare.util.SimpleSWORDDeposit";


    public SimpleSWORDDeposit(String filename, String mime, Hashtable<String, String> metadata, FileOutputStream fosmets) throws Exception {
        // First, compile the mets.xml, then save it temporarily
        String mets = makeMets(filename.substring(filename.lastIndexOf('/') + 1), mime, metadata);
        System.out.println(mets);
        fosmets.write(mets.getBytes());
        fosmets.close();
    }

    public void makePackage(InputStream thePackage, String filename, FileOutputStream foszip, FileInputStream fismets) throws Exception {
        // Now make the package
        Log.d(TAG, "Opening zip file for writing");
        ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(foszip));
        byte data[] = new byte[2048];
        BufferedInputStream origin;

        Log.d(TAG, "Adding mets.xml to zip file");
        origin = new BufferedInputStream(fismets, 2048);
        ZipEntry entry = new ZipEntry("mets.xml");
        zip.putNextEntry(entry);
        int count2;
        while((count2 = origin.read(data, 0, 2048)) != -1) {
           zip.write(data, 0, count2);
        }
        origin.close();

        Log.d(TAG, "Adding content file to zip file");
        origin = new BufferedInputStream(thePackage, 2048);
        ZipEntry entry2 = new ZipEntry(filename.substring(filename.lastIndexOf('/') + 1));
        zip.putNextEntry(entry2);
        int count;
        while((count = origin.read(data, 0, 2048)) != -1) {
           zip.write(data, 0, count);
        }
        origin.close();

        Log.d(TAG, "Closing zip file");
        zip.close();
    }

    public boolean deposit(InputStream fis, String theUrl, String username, String password) throws Exception {
        // Setup the http connection
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        URL url = new URL(theUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);

        // Set the authentication headers
        String encodedAuthorization = Base64.encodeBytes((username + ":" + password).getBytes());
        conn.setRequestProperty("Authorization", "Basic " + encodedAuthorization);

        // Set the http headers
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("Content-Type", "application/zip");
        conn.setRequestProperty("X-Packaging", "http://purl.org/net/sword-types/METSDSpaceSIP");

        // Send the file
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        bytesAvailable = fis.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];
        bytesRead = fis.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fis.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fis.read(buffer, 0, bufferSize);
        }

        // Get the response from the server
        int serverResponseCode = conn.getResponseCode();
        fis.close();
        dos.flush();
        dos.close();
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        String atom = "";
        while ((line = rd.readLine()) != null) {
            atom += line + "\n";
        }
        rd.close();

        xml = atom;

        // Return whether it completed OK or not
        return ((serverResponseCode >= 200) && (serverResponseCode < 300));
    }

    public String getURL() {
        String id = xml.substring(xml.indexOf("<atom:id>") + 9);
        id = id.substring(0, id.indexOf('<'));
        return id;
    }

    private String makeMets(String filename, String mime, Hashtable<String, String> metadata) {
        StringBuilder mets = new StringBuilder();
        mets.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n")
        .append("<mets ID=\"sort-mets_mets\" OBJID=\"sword-mets\" LABEL=\"DSpace SWORD Item\"\n")
        .append("    PROFILE=\"DSpace METS SIP Profile 1.0\" xmlns=\"http://www.loc.gov/METS/\"\n")
        .append("    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n")
        .append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n")
        .append("    xsi:schemaLocation=\"http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd\">\n")
        .append("\n")
        .append("    <metsHdr CREATEDATE=\"2008-09-04T00:00:00\">\n")
        .append("        <agent ROLE=\"CUSTODIAN\" TYPE=\"ORGANIZATION\">\n")
        .append("            <name>").append(metadata.get("creator")).append("</name>\n")
        .append("        </agent>\n")
        .append("    </metsHdr>\n")
	    .append("\n")
        .append("    <dmdSec ID=\"sword-mets-dmd-1\" GROUPID=\"sword-mets-dmd-1_group-1\">\n")
        .append("        <mdWrap LABEL=\"SWAP Metadata\" MDTYPE=\"OTHER\" OTHERMDTYPE=\"EPDCX\"\n")
        .append("            MIMETYPE=\"text/xml\">\n")
        .append("\n")
        .append("            <xmlData>\n")
        .append("                <epdcx:descriptionSet\n")
        .append("                    xmlns:epdcx=\"http://purl.org/eprint/epdcx/2006-11-16/\"\n")
        .append("                    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n")
        .append("                    xsi:schemaLocation=\"http://purl.org/eprint/epdcx/2006-11-16 http://purl.org/eprint/epdcx/xsd/2006-11-16/epdcx.xsd\">\n")
        .append("\n")
        .append("                    <epdcx:description\n")
        .append("                        epdcx:resourceId=\"sword-mets-epdcx-1\">\n")
        .append("                        <epdcx:statement\n")
        .append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/type\"\n")
        .append("                            epdcx:valueURI=\"http://purl.org/eprint/entityType/ScholarlyWork\" />\n")
        .append("                        <epdcx:statement\n")
        .append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/title\">\n")
        .append("                            <epdcx:valueString>").append(metadata.get("title")).append("</epdcx:valueString>\n")
        .append("                        </epdcx:statement>\n")
        .append("                        <epdcx:statement\n")
        .append("                            epdcx:propertyURI=\"http://purl.org/dc/terms/abstract\">\n")
        .append("                            <epdcx:valueString>").append(metadata.get("description")).append("</epdcx:valueString>\n")
        .append("                        </epdcx:statement>\n")
        .append("                        <epdcx:statement\n")
        .append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/creator\">\n")
        .append("                            <epdcx:valueString>").append(metadata.get("creator")).append("</epdcx:valueString>\n")
        .append("                        </epdcx:statement>\n")
        .append("                        <epdcx:statement\n")
        .append("                            epdcx:propertyURI=\"http://purl.org/eprint/terms/isExpressedAs\"\n")
        .append("                            epdcx:valueRef=\"sword-mets-expr-1\" />\n")
        .append("                    </epdcx:description>\n")
        .append("\n")
        .append("                    <epdcx:description\n")
        .append("                        epdcx:resourceId=\"sword-mets-expr-1\">\n")
        .append("                        <epdcx:statement\n")
        .append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/type\"\n")
        .append("                            epdcx:valueURI=\"http://purl.org/eprint/entityType/Expression\" />\n")
        .append("                        <epdcx:statement\n")
        .append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/language\"\n")
        .append("                            epdcx:vesURI=\"http://purl.org/dc/terms/RFC3066\">\n")
        .append("                            <epdcx:valueString>en</epdcx:valueString>\n")
        .append("                        </epdcx:statement>\n")
        .append("                        <epdcx:statement\n")
        .append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/type\"\n")
        .append("                            epdcx:vesURI=\"http://purl.org/eprint/terms/Type\"\n")
        .append("                            epdcx:valueURI=\"http://purl.org/eprint/type/JournalArticle\" />\n")
        .append("                        <epdcx:statement\n")
        .append("                            epdcx:propertyURI=\"http://purl.org/dc/terms/available\">\n")
        .append("                            <epdcx:valueString\n")
        .append("                                epdcx:sesURI=\"http://purl.org/dc/terms/W3CDTF\">\n")
        .append("                                2008-01\n")
        .append("                            </epdcx:valueString>\n")
        .append("                        </epdcx:statement>\n")
        .append("                    </epdcx:description>\n")
        .append("                </epdcx:descriptionSet>\n")
        .append("            </xmlData>\n")
        .append("        </mdWrap>\n")
        .append("    </dmdSec>\n")
        .append("\n")
        .append("    <fileSec>\n")
        .append("        <fileGrp ID=\"sword-mets-fgrp-1\" USE=\"CONTENT\">\n")
        .append("            <file GROUPID=\"sword-mets-fgid-0\" ID=\"sword-mets-file-1\"\n")
        .append("                MIMETYPE=\"").append(mime).append("\">\n")
        .append("                <FLocat LOCTYPE=\"URL\" xlink:href=\"").append(filename).append("\" />\n")
        .append("            </file>\n")
        .append("        </fileGrp>\n")
        .append("    </fileSec>\n")
	    .append("\n")
        .append("    <structMap ID=\"sword-mets-struct-1\" LABEL=\"structure\"\n")
        .append("        TYPE=\"LOGICAL\">\n")
        .append("        <div ID=\"sword-mets-div-1\" DMDID=\"sword-mets-dmd-1\" TYPE=\"SWORD Object\">\n")
        .append("            <div ID=\"sword-mets-div-2\" TYPE=\"File\">\n")
        .append("                <fptr FILEID=\"sword-mets-file-1\" />\n")
        .append("            </div>\n")
        .append("        </div>\n")
        .append("    </structMap>\n")
        .append("\n")
        .append("</mets>\n");

        return mets.toString();
    }

    public static void main(String[] args) throws Exception {
        Hashtable<String, String> metadata = new Hashtable<String, String>();
        metadata.put("creator", "Lewis, Stuart");
        metadata.put("title", "Test title");
        metadata.put("description", "Test description");

        String metsfilename = "/Users/stuartlewis/Desktop/mets.xml";
        String zipfilename = "/Users/stuartlewis/Desktop/package.xml";
        FileOutputStream fosmets = new FileOutputStream(new File(metsfilename));
        FileOutputStream foszip = new FileOutputStream(new File(zipfilename));

        String filename = "/Library/WebServer/Documents/swordappv2-php-library/test/test-files/mets_swap/SWORD Ariadne Jan 2008.pdf";

        SimpleSWORDDeposit deposit = new SimpleSWORDDeposit(filename, "application/pdf", metadata, fosmets);

        FileInputStream fismets = new FileInputStream(metsfilename);
        FileInputStream thePackage = new FileInputStream(new File(filename));
        deposit.makePackage(thePackage, filename, foszip, fismets);
        FileInputStream fospackage = new FileInputStream(new File(zipfilename));
        deposit.deposit(fospackage, "http://192.168.2.247:8080/sword/deposit/123456789/766", "sword@swordapp.org", "sword");
    }
}
