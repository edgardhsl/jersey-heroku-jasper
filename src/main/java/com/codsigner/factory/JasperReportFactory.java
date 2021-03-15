package com.codsigner.factory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.lowagie.text.pdf.PdfWriter;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

public class JasperReportFactory {

    private static final Path root = (Paths.get(".").normalize().toAbsolutePath());
    private static final String assetsDir = "/src/assets";
    private static final String reportDir = "/src/assets/jrxml";

    public static byte[] print(String jrxmlFile, Map < String, Object > paramns, ArrayList < HashMap < String, Object >> rows) {
        try {
            if (paramns == null) {
                paramns = new HashMap < String, Object > ();
            }

            String reportFileDir = root.toAbsolutePath() + reportDir + '/' + jrxmlFile;

            if (new File(reportFileDir + ".jrxml").isFile() == false) {
                throw new Exception("Arquivo de relatório não encontrado");
            }

            paramns.put("report_assets", root.toAbsolutePath() + JasperReportFactory.assetsDir);

            JasperReport reportFile = JasperReportFactory.getReportFile(reportFileDir);
            JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(rows);

            JasperPrint print = JasperFillManager.fillReport(reportFile, paramns, datasource);
            ByteArrayOutputStream out = new ByteArrayOutputStream();


            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(print));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(out));
            SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();

            configuration.setPermissions(PdfWriter.ALLOW_COPY | PdfWriter.ALLOW_PRINTING);
            exporter.setConfiguration(configuration);
            exporter.exportReport();

            return out.toByteArray();
        } catch (JRException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static JasperReport getReportFile(String filePath) {
        try {
            JasperReport reportFile;

            if (new File(filePath + ".jasper").exists()) {
                System.out.print("Load");
                reportFile = (JasperReport) JRLoader.loadObjectFromFile(filePath + ".jasper");
            } else {
                System.out.print(filePath + ".jrxml");
                reportFile = JasperCompileManager.compileReport(filePath + ".jrxml");
            }

            return reportFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
