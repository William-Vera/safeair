package com.example.safeairmobile.clases;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class myclass {
    public void generateAndSavePdf(LineChart lineChart, Context context) {
        PdfDocument pdfDocument = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(lineChart.getWidth(), lineChart.getHeight(), 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        lineChart.draw(canvas);
        pdfDocument.finishPage(page);

        // Guardar el PDF en el almacenamiento externo
        String fileName = "grafico.pdf";
        File file = new File(Environment.getExternalStorageDirectory(), fileName);
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            Toast.makeText(context, "PDF descargado exitosamente", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error al descargar el PDF", Toast.LENGTH_SHORT).show();
        }
        pdfDocument.close();
    }
}
