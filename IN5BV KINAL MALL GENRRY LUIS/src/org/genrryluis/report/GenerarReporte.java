package org.genrryluis.report;

import java.io.InputStream;
import java.util.Map;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.genrryluis.db.Conexion;

/**
 *
 * @author Genrry Luis
 */
public class GenerarReporte {

    private static GenerarReporte instancia;

    private GenerarReporte() {
    }

    public static GenerarReporte getInstance() {
        if (instancia == null) {
            instancia = new GenerarReporte();
        }
        return instancia;
    }

    public void mostrarReporte(String nombreReporteJasper, String titulo, Map parametros) {

        try {

            parametros.put("LOGO_HEADER", getClass().getResourceAsStream("/org/genrryluis/resource/images/Kinal1.png"));
            parametros.put("LOGO_FOOTER", getClass().getResourceAsStream("/org/genrryluis/resource/images/logotipoKinal.jpg"));
            parametros.put("LOGO_DETAIL", getClass().getResourceAsStream("/org/genrryluis/resource/images/logotipoKinal.jpg"));
            /*
            LOGO_HEADER, $P{LOGO_FOOTER, $P{LOGO_DETAIL}*/

            InputStream archivo = GenerarReporte.class.getResourceAsStream(nombreReporteJasper);
            JasperReport report = (JasperReport) JRLoader.loadObject(archivo);
            JasperPrint print = JasperFillManager.fillReport(report, parametros, Conexion.getInstance().getConexion());
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle(titulo);
            viewer.setVisible(true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
