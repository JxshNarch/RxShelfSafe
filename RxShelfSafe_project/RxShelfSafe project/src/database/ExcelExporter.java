package database;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import model.Batch;

// Saves the report as a real Excel (.xlsx) file.
// An .xlsx file is just a zip file with small XML files inside,
// so we can build it with java.util.zip (no extra library needed).
public class ExcelExporter {

    public static void export(File file, ArrayList<Batch> rows) throws IOException {
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));

        addToZip(zip, "[Content_Types].xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<Types xmlns=\"http://schemas.openxmlformats.org/package/2006/content-types\">"
            + "<Default Extension=\"rels\" ContentType=\"application/vnd.openxmlformats-package.relationships+xml\"/>"
            + "<Default Extension=\"xml\" ContentType=\"application/xml\"/>"
            + "<Override PartName=\"/xl/workbook.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml\"/>"
            + "<Override PartName=\"/xl/worksheets/sheet1.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml\"/>"
            + "<Override PartName=\"/xl/styles.xml\" ContentType=\"application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml\"/>"
            + "</Types>");

        addToZip(zip, "_rels/.rels",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument\" Target=\"xl/workbook.xml\"/>"
            + "</Relationships>");

        addToZip(zip, "xl/workbook.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<workbook xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\" "
            + "xmlns:r=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships\">"
            + "<sheets><sheet name=\"Expiry Report\" sheetId=\"1\" r:id=\"rId1\"/></sheets></workbook>");

        addToZip(zip, "xl/_rels/workbook.xml.rels",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<Relationships xmlns=\"http://schemas.openxmlformats.org/package/2006/relationships\">"
            + "<Relationship Id=\"rId1\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet\" Target=\"worksheets/sheet1.xml\"/>"
            + "<Relationship Id=\"rId2\" Type=\"http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles\" Target=\"styles.xml\"/>"
            + "</Relationships>");

        // styles: style 0 = normal text, style 1 = bold text (for the header row)
        addToZip(zip, "xl/styles.xml",
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<styleSheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">"
            + "<fonts count=\"2\"><font><sz val=\"11\"/><name val=\"Calibri\"/></font>"
            + "<font><b/><sz val=\"11\"/><name val=\"Calibri\"/></font></fonts>"
            + "<fills count=\"2\"><fill><patternFill patternType=\"none\"/></fill><fill><patternFill patternType=\"gray125\"/></fill></fills>"
            + "<borders count=\"1\"><border><left/><right/><top/><bottom/><diagonal/></border></borders>"
            + "<cellStyleXfs count=\"1\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\"/></cellStyleXfs>"
            + "<cellXfs count=\"2\"><xf numFmtId=\"0\" fontId=\"0\" fillId=\"0\" borderId=\"0\" xfId=\"0\"/>"
            + "<xf numFmtId=\"0\" fontId=\"1\" fillId=\"0\" borderId=\"0\" xfId=\"0\" applyFont=\"1\"/></cellXfs>"
            + "</styleSheet>");

        addToZip(zip, "xl/worksheets/sheet1.xml", makeSheet(rows));
        zip.close();
    }

    // write one small file into the zip
    private static void addToZip(ZipOutputStream zip, String name, String text) throws IOException {
        zip.putNextEntry(new ZipEntry(name));
        zip.write(text.getBytes("UTF-8"));
        zip.closeEntry();
    }

    // build the sheet: the first row is the header, then one row per batch
    private static String makeSheet(ArrayList<Batch> rows) {
        String[] columns = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
        String[] headers = {"Batch ID", "Medicine Name", "Category", "Dosage", "Quantity",
                            "Expiry Date", "Days Left", "Status", "Checked"};

        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<worksheet xmlns=\"http://schemas.openxmlformats.org/spreadsheetml/2006/main\">");
        xml.append("<cols><col min=\"1\" max=\"9\" width=\"16\" customWidth=\"1\"/></cols>");
        xml.append("<sheetData>");

        // header row (bold)
        xml.append("<row r=\"1\">");
        for (int c = 0; c < headers.length; c++) {
            xml.append(textCell(columns[c] + "1", headers[c], 1));
        }
        xml.append("</row>");

        // one row for each batch
        for (int i = 0; i < rows.size(); i++) {
            Batch b = rows.get(i);
            int r = i + 2;                       // row 1 is the header
            String checked = "";
            if (b.isChecked()) {
                checked = "✓";              // a check mark
            }
            xml.append("<row r=\"" + r + "\">");
            xml.append(textCell("A" + r, b.getBatchId(), 0));
            xml.append(textCell("B" + r, b.getName(), 0));
            xml.append(textCell("C" + r, b.getCategory(), 0));
            xml.append(textCell("D" + r, b.getDosage(), 0));
            xml.append(numberCell("E" + r, b.getQuantity()));
            xml.append(textCell("F" + r, b.getExpiration().format(Batch.DATE_FORMAT), 0));
            xml.append(numberCell("G" + r, b.getDaysLeft()));
            xml.append(textCell("H" + r, b.getStatus(), 0));
            xml.append(textCell("I" + r, checked, 0));
            xml.append("</row>");
        }

        xml.append("</sheetData>");
        xml.append("</worksheet>");
        return xml.toString();
    }

    private static String textCell(String ref, String text, int style) {
        return "<c r=\"" + ref + "\" s=\"" + style + "\" t=\"inlineStr\"><is><t>"
                + safe(text) + "</t></is></c>";
    }

    private static String numberCell(String ref, long number) {
        return "<c r=\"" + ref + "\"><v>" + number + "</v></c>";
    }

    // the characters & < > have a special meaning in XML, so we replace them
    private static String safe(String text) {
        text = text.replace("&", "&amp;");
        text = text.replace("<", "&lt;");
        text = text.replace(">", "&gt;");
        return text;
    }
}
