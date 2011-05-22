package org.skylightui.swordshare.util;

import org.apache.http.client.HttpClient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SimpleSWORDDeposit {

    public SimpleSWORDDeposit(String url, String user, String password,
                              String filename, String mime, Hashtable<String, ArrayList<String>> metadata,
                              String metsfilename, FileOutputStream fosmets,
                              String zipfilename, FileOutputStream foszip)
                              throws Exception {
        // First, compile the mets.xml, then save it temporarily
        String mets = makeMets(filename.substring(filename.lastIndexOf('/') + 1), mime, metadata);
        System.out.println(mets);
        fosmets.write(mets.getBytes());
        fosmets.close();

        // Now make the package
        String[] filenames = new String[]{metsfilename, filename};
        ZipOutputStream zip = new ZipOutputStream(new BufferedOutputStream(foszip));
        byte data[] = new byte[2048];
        BufferedInputStream origin = null;
        for (String fname : filenames) {
            System.out.println("Adding: " + fname + " as " + fname.substring(fname.lastIndexOf('/') + 1));
            FileInputStream fi = new FileInputStream(fname);
            origin = new BufferedInputStream(fi, 2048);
            ZipEntry entry = new ZipEntry(fname.substring(fname.lastIndexOf('/') + 1));
            zip.putNextEntry(entry);
            int count;
            while((count = origin.read(data, 0, 2048)) != -1) {
               zip.write(data, 0, count);
            }
            origin.close();
         }
        zip.close();

        // Next deposit the package
        this.upload(zipfilename, url, user, password);

        // Finally get the response
    }

    private String makeMets(String filename, String mime, Hashtable<String, ArrayList<String>>metadata) {
        StringBuilder mets = new StringBuilder();
        mets.append("<?xml version=\"1.0\" encoding=\"utf-8\" standalone=\"no\"?>\n");
        mets.append("<mets ID=\"sort-mets_mets\" OBJID=\"sword-mets\" LABEL=\"DSpace SWORD Item\"\n");
        mets.append("    PROFILE=\"DSpace METS SIP Profile 1.0\" xmlns=\"http://www.loc.gov/METS/\"\n");
        mets.append("    xmlns:xlink=\"http://www.w3.org/1999/xlink\"\n");
        mets.append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        mets.append("    xsi:schemaLocation=\"http://www.loc.gov/METS/ http://www.loc.gov/standards/mets/mets.xsd\">\n");
        mets.append("\n");
        mets.append("    <metsHdr CREATEDATE=\"2008-09-04T00:00:00\">\n");
        mets.append("        <agent ROLE=\"CUSTODIAN\" TYPE=\"ORGANIZATION\">\n");
        mets.append("            <name>Stuart Lewis</name>\n");
        mets.append("        </agent>\n");
        mets.append("    </metsHdr>\n");
	    mets.append("\n");
        mets.append("    <dmdSec ID=\"sword-mets-dmd-1\" GROUPID=\"sword-mets-dmd-1_group-1\">\n");
        mets.append("        <mdWrap LABEL=\"SWAP Metadata\" MDTYPE=\"OTHER\" OTHERMDTYPE=\"EPDCX\"\n");
        mets.append("            MIMETYPE=\"text/xml\">\n");
        mets.append("\n");
        mets.append("            <xmlData>\n");
        mets.append("                <epdcx:descriptionSet\n");
        mets.append("                    xmlns:epdcx=\"http://purl.org/eprint/epdcx/2006-11-16/\"\n");
        mets.append("                    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        mets.append("                    xsi:schemaLocation=\"http://purl.org/eprint/epdcx/2006-11-16 http://purl.org/eprint/epdcx/xsd/2006-11-16/epdcx.xsd\">\n");
        mets.append("\n");
        mets.append("                    <epdcx:description\n");
        mets.append("                        epdcx:resourceId=\"sword-mets-epdcx-1\">\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/type\"\n");
        mets.append("                            epdcx:valueURI=\"http://purl.org/eprint/entityType/ScholarlyWork\" />\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/title\">\n");
        mets.append("                            <epdcx:valueString>\n");
        mets.append("                                SWORD: Simple Web-service Offering Repository Deposit\n");
        mets.append("                            </epdcx:valueString>\n");
        mets.append("                        </epdcx:statement>\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/dc/terms/abstract\">\n");
        mets.append("                            <epdcx:valueString>\n");
        mets.append("                                Abstract\n");
        mets.append("                            </epdcx:valueString>\n");
        mets.append("                        </epdcx:statement>\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/creator\">\n");
        mets.append("                            <epdcx:valueString>\n");
        mets.append("                                Allinson, Julie\n");
        mets.append("                            </epdcx:valueString>\n");
        mets.append("                        </epdcx:statement>\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/identifier\">\n");
        mets.append("                            <epdcx:valueString\n");
        mets.append("                                epdcx:sesURI=\"http://purl.org/dc/terms/URI\">\n");
        mets.append("                                http://www.ariadne.ac.uk/issue54/allinson-et-al/\n");
        mets.append("                            </epdcx:valueString>\n");
        mets.append("                        </epdcx:statement>\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/eprint/terms/isExpressedAs\"\n");
        mets.append("                            epdcx:valueRef=\"sword-mets-expr-1\" />\n");
        mets.append("                    </epdcx:description>\n");
        mets.append("\n");
        mets.append("                    <epdcx:description\n");
        mets.append("                        epdcx:resourceId=\"sword-mets-expr-1\">\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/type\"\n");
        mets.append("                            epdcx:valueURI=\"http://purl.org/eprint/entityType/Expression\" />\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/language\"\n");
        mets.append("                            epdcx:vesURI=\"http://purl.org/dc/terms/RFC3066\">\n");
        mets.append("                            <epdcx:valueString>en</epdcx:valueString>\n");
        mets.append("                        </epdcx:statement>\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/dc/elements/1.1/type\"\n");
        mets.append("                            epdcx:vesURI=\"http://purl.org/eprint/terms/Type\"\n");
        mets.append("                            epdcx:valueURI=\"http://purl.org/eprint/type/JournalArticle\" />\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/dc/terms/available\">\n");
        mets.append("                            <epdcx:valueString\n");
        mets.append("                                epdcx:sesURI=\"http://purl.org/dc/terms/W3CDTF\">\n");
        mets.append("                                2008-01\n");
        mets.append("                            </epdcx:valueString>\n");
        mets.append("                        </epdcx:statement>\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/eprint/terms/Status\"\n");
        mets.append("                            epdcx:vesURI=\"http://purl.org/eprint/terms/Status\"\n");
        mets.append("                            epdcx:valueURI=\"http://purl.org/eprint/status/PeerReviewed\" />\n");
        mets.append("                        <epdcx:statement\n");
        mets.append("                            epdcx:propertyURI=\"http://purl.org/eprint/terms/copyrightHolder\">\n");
        mets.append("                            <epdcx:valueString>\n");
        mets.append("                                Julie Allinson, Sebastien François, Stuart Lewis\n");
        mets.append("                            </epdcx:valueString>\n");
        mets.append("                        </epdcx:statement>\n");
        mets.append("                    </epdcx:description>\n");
        mets.append("                </epdcx:descriptionSet>\n");
        mets.append("            </xmlData>\n");
        mets.append("        </mdWrap>\n");
        mets.append("    </dmdSec>\n");
        mets.append("\n");
        mets.append("    <fileSec>\n");
        mets.append("        <fileGrp ID=\"sword-mets-fgrp-1\" USE=\"CONTENT\">\n");
        mets.append("            <file GROUPID=\"sword-mets-fgid-0\" ID=\"sword-mets-file-1\"\n");
        mets.append("                MIMETYPE=\"" + mime + "\">\n");
        mets.append("                <FLocat LOCTYPE=\"URL\" xlink:href=\"" + filename + "\" />\n");
        mets.append("            </file>\n");
        mets.append("        </fileGrp>\n");
        mets.append("    </fileSec>\n");
	    mets.append("\n");
        mets.append("    <structMap ID=\"sword-mets-struct-1\" LABEL=\"structure\"\n");
        mets.append("        TYPE=\"LOGICAL\">\n");
        mets.append("        <div ID=\"sword-mets-div-1\" DMDID=\"sword-mets-dmd-1\" TYPE=\"SWORD Object\">\n");
        mets.append("            <div ID=\"sword-mets-div-2\" TYPE=\"File\">\n");
        mets.append("                <fptr FILEID=\"sword-mets-file-1\" />\n");
        mets.append("            </div>\n");
        mets.append("        </div>\n");
        mets.append("    </structMap>\n");
        mets.append("\n");
        mets.append("</mets>\n");

        return mets.toString();
    }

    private void upload(String source, String theUrl, String username, String password) throws Exception {
        // Setup the http connection
        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(source);
        FileInputStream fileInputStream = new FileInputStream(sourceFile);
        URL url = new URL(theUrl);
        conn = (HttpURLConnection) url.openConnection();
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
        dos = new DataOutputStream(conn.getOutputStream());
        bytesAvailable = fileInputStream.available();
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        while (bytesRead > 0) {
            dos.write(buffer, 0, bufferSize);
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
        }

        // Get the response from the server
        int serverResponseCode = conn.getResponseCode();
        String serverResponseMessage = conn.getResponseMessage();
        System.out.println("Upload file to server: HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
        System.out.println("Upload file to server: " + source + " File is written");
        fileInputStream.close();
        dos.flush();
        dos.close();
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            System.out.println("In: " + line);
        }
        rd.close();
    }

    public static void main(String[] args) throws Exception {
        Hashtable<String, ArrayList<String>> metadata = new Hashtable<String, ArrayList<String>>();
        String metsfilename = "/Users/stuartlewis/Desktop/mets.xml";
        String zipfilename = "/Users/stuartlewis/Desktop/package.xml";
        FileOutputStream fosmets = new FileOutputStream(new File(metsfilename));
        FileOutputStream foszip = new FileOutputStream(new File(zipfilename));
        SimpleSWORDDeposit deposit = new SimpleSWORDDeposit("http://localhost:8080/sword/deposit/123456789/766",
                                                            "stuart@stuartlewis.com", "123456",
                                                            "/Library/WebServer/Documents/swordappv2-php-library/test/test-files/mets_swap/SWORD Ariadne Jan 2008.pdf",
                                                            "application/pdf", metadata, metsfilename, fosmets,
                                                            zipfilename, foszip);
    }
}
