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
    private static final String assetsDir = "/assets";
    private static final String reportDir = "/assets/jrxml";
    
    public static byte[] print(String jrxmlFile, Map<String, Object> paramns, ArrayList<HashMap<String, Object>> rows) {
        try {

            if(paramns == null) {
                paramns = new HashMap<String, Object>();
            }

            System.out.println(root.toAbsolutePath() + reportDir + '/' + jrxmlFile);

            paramns.put("report_assets", JasperReportFactory.assetsDir);

            JasperReport reportFile = JasperReportFactory.getReportFile(root.toAbsolutePath() + reportDir + '/' + jrxmlFile + ".jrxml");
            JRBeanCollectionDataSource datasource = new JRBeanCollectionDataSource(rows);	
                        
            System.out.println("--------- LOG --------");
            System.out.println(reportFile);
            System.out.println(paramns);
            System.out.println(datasource);

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
        }
    }

    public static JasperReport getReportFile(String filePath) {
        try {
            JasperReport reportFile;
	    	
			if(new File(filePath+".jasper").exists()) {
				reportFile = (JasperReport)JRLoader.loadObjectFromFile(filePath+".jasper");
			}
			else {
				reportFile = JasperCompileManager.compileReport(filePath+".jrxml");
			}
			
			return reportFile;
        } catch (Exception e) {
            return null;
        }
    }
}
