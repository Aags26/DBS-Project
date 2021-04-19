package com.bphc.dbs_project.helper;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.bphc.dbs_project.R;
import com.bphc.dbs_project.models.MedicalRecord;
import com.bphc.dbs_project.prefs.SharedPrefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import static com.bphc.dbs_project.prefs.SharedPrefsConstants.NAME;

public class PdfHelper {

    public static String generateReport(ArrayList<MedicalRecord> medicalRecords, String name, Context context) {
        PdfDocument pdfDocument = new PdfDocument();
        Paint title = new Paint();

        int length = (int)(Math.ceil(medicalRecords.size()/4.0));

        PdfDocument.PageInfo[] myPageInfo = new PdfDocument.PageInfo[length];
        myPageInfo[0] = new PdfDocument.PageInfo.Builder(1120, 792, 1).create();
        PdfDocument.Page[] myPage = new PdfDocument.Page[length];
        myPage[0] = pdfDocument.startPage(myPageInfo[0]);
        Canvas[] canvas = new Canvas[length];
        canvas[0] = myPage[0].getCanvas();

        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setTextSize(35);
        title.setColor(ContextCompat.getColor(context, R.color.black));
        title.setTextAlign(Paint.Align.CENTER);
        title.setFlags(Paint.UNDERLINE_TEXT_FLAG);
        canvas[0].drawText("Medical Records of " + name, 560, 60, title);

        title.reset();
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        title.setTextSize(25);
        title.setTextAlign(Paint.Align.LEFT);
        int i = 120;
        int k = 0;
        for (int j = 0; j < medicalRecords.size(); j++) {
            if (j >= 4 && (j + 1) % 4 == 1) {
                i = 120;
                pdfDocument.finishPage(myPage[k]);
                k++;
                myPageInfo[k] = new PdfDocument.PageInfo.Builder(1120, 792, 1).create();
                myPage[k] = pdfDocument.startPage(myPageInfo[k]);
                canvas[k] = myPage[k].getCanvas();
            }
            MedicalRecord medicalRecord = medicalRecords.get(j);
            canvas[k].drawText("AILMENT:     " + medicalRecord.getDiagnosis(), 56, i, title);
            i += 30;
            canvas[k].drawText("DATE OF EXAMINATION:     " + medicalRecord.getDoe(), 56, i, title);
            i += 30;
            canvas[k].drawText("DOCTOR:     " + medicalRecord.getDescription().split("\\|")[0], 56, i, title);
            i += 30;
            canvas[k].drawText("DESCRIPTION:     " + medicalRecord.getDescription().split("\\|")[1], 56, i, title);
            i += 40;
            canvas[k].drawLine(56, i, 1064, i, title);
            i += 40;
        }
        pdfDocument.finishPage(myPage[k]);
        File file = new File(Environment.getExternalStorageDirectory(), "Medical Records - " + name + ".pdf");
        String s = "";
        try {
            pdfDocument.writeTo(new FileOutputStream(file));
            s = "PDF file generated succesfully.";
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        pdfDocument.close();
        return s;
    }

}
