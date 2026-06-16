package com.shiftscheduler.service;

import com.shiftscheduler.model.JadwalHasil;
import com.shiftscheduler.model.Karyawan;
import com.shiftscheduler.model.Shift;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service untuk mengekspor data jadwal ke format CSV dan PDF.
 */
public class ExportService {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_FORMAT_FILE = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Mengekspor jadwal ke file CSV.
     * Format: Tanggal, Nama Karyawan, Posisi, Kode Shift, Jam Mulai, Jam Selesai
     * 
     * @param jadwalList daftar jadwal yang akan diekspor
     * @param targetFile file tujuan (.csv)
     * @throws IOException jika terjadi error saat menulis file
     */
    public void exportCSV(List<JadwalHasil> jadwalList, File targetFile) throws IOException {
        try (CSVWriter writer = new CSVWriter(new FileWriter(targetFile))) {
            // Header
            String[] header = {"Tanggal", "Nama Karyawan", "Posisi", "Kode Shift", "Jam Mulai", "Jam Selesai"};
            writer.writeNext(header);

            // Data
            for (JadwalHasil j : jadwalList) {
                String[] row = {
                    j.getTanggal().format(DATE_FORMAT),
                    j.getNamaKaryawan(),
                    j.getPosisiKaryawan(),
                    j.getKodeShift(),
                    j.getJamMulai(),
                    j.getJamSelesai()
                };
                writer.writeNext(row);
            }
        }
    }

    /**
     * Mengekspor jadwal ke file PDF dalam format tabel matriks.
     * Baris = Karyawan, Kolom = Tanggal, Sel = Kode Shift
     * 
     * @param jadwalList daftar jadwal yang akan diekspor
     * @param targetFile file tujuan (.pdf)
     * @param daftarKaryawan daftar semua karyawan (untuk baris tabel)
     * @param daftarShift daftar semua shift (untuk legenda)
     * @throws IOException jika terjadi error saat membuat PDF
     */
    public void exportPDF(List<JadwalHasil> jadwalList, File targetFile,
                          List<Karyawan> daftarKaryawan, List<Shift> daftarShift) throws IOException {
        // Kumpulkan tanggal unik dan sort
        Set<LocalDate> tanggalSet = new TreeSet<>();
        for (JadwalHasil j : jadwalList) {
            tanggalSet.add(j.getTanggal());
        }
        List<LocalDate> daftarTanggal = new ArrayList<>(tanggalSet);

        // Buat lookup map: (idKaryawan, tanggal) → kodeShift
        Map<String, String> jadwalMap = new HashMap<>();
        for (JadwalHasil j : jadwalList) {
            String key = j.getIdKaryawan() + "-" + j.getTanggal();
            jadwalMap.put(key, j.getKodeShift());
        }

        try (PdfWriter writer = new PdfWriter(targetFile);
             PdfDocument pdf = new PdfDocument(writer);
             Document doc = new Document(pdf, PageSize.A4.rotate())) {

            doc.setMargins(20, 20, 20, 20);

            // === JUDUL ===
            Paragraph title = new Paragraph("JADWAL SHIFT KARYAWAN")
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(5);
            doc.add(title);

            // Subtitle: Rentang tanggal
            if (!daftarTanggal.isEmpty()) {
                String subtitle = "Periode: " + daftarTanggal.get(0).format(DATE_FORMAT)
                    + " s/d " + daftarTanggal.get(daftarTanggal.size() - 1).format(DATE_FORMAT);
                doc.add(new Paragraph(subtitle)
                    .setFontSize(11)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(15));
            }

            // === TABEL MATRIKS ===
            int numCols = daftarTanggal.size() + 2; // No. + Nama + tanggal-tanggal
            Table table = new Table(UnitValue.createPercentArray(numCols))
                .useAllAvailableWidth();

            // Warna header
            DeviceRgb headerBg = new DeviceRgb(41, 128, 185);
            DeviceRgb altRowBg = new DeviceRgb(235, 245, 251);

            // Header row
            table.addHeaderCell(createHeaderCell("No.", headerBg));
            table.addHeaderCell(createHeaderCell("Nama Karyawan", headerBg));
            for (LocalDate tgl : daftarTanggal) {
                DateTimeFormatter shortFmt = DateTimeFormatter.ofPattern("dd/MM\nEEE", new java.util.Locale("id"));
                table.addHeaderCell(createHeaderCell(tgl.format(shortFmt), headerBg));
            }

            // Data rows
            int rowNum = 0;
            for (Karyawan k : daftarKaryawan) {
                rowNum++;
                DeviceRgb rowBg = (rowNum % 2 == 0) ? altRowBg : null;

                // Kolom No.
                Cell noCell = new Cell().add(new Paragraph(String.valueOf(rowNum)).setFontSize(9));
                if (rowBg != null) noCell.setBackgroundColor(rowBg);
                table.addCell(noCell);

                // Kolom Nama
                Cell namaCell = new Cell().add(new Paragraph(k.getNama()).setFontSize(9));
                if (rowBg != null) namaCell.setBackgroundColor(rowBg);
                table.addCell(namaCell);

                // Kolom shift per tanggal
                for (LocalDate tgl : daftarTanggal) {
                    String key = k.getId() + "-" + tgl;
                    String kode = jadwalMap.getOrDefault(key, "-");
                    Cell shiftCell = new Cell()
                        .add(new Paragraph(kode).setFontSize(9).setTextAlignment(TextAlignment.CENTER));
                    if (rowBg != null) shiftCell.setBackgroundColor(rowBg);

                    // Warnai berdasarkan shift
                    if ("P".equals(kode)) {
                        shiftCell.setBackgroundColor(new DeviceRgb(255, 243, 205)); // kuning muda
                    } else if ("S".equals(kode)) {
                        shiftCell.setBackgroundColor(new DeviceRgb(209, 236, 241)); // biru muda
                    } else if ("M".equals(kode)) {
                        shiftCell.setBackgroundColor(new DeviceRgb(215, 204, 232)); // ungu muda
                    }

                    table.addCell(shiftCell);
                }
            }

            doc.add(table);

            // === LEGENDA ===
            doc.add(new Paragraph("\nKeterangan Shift:").setBold().setFontSize(10).setMarginTop(10));
            for (Shift s : daftarShift) {
                doc.add(new Paragraph("  " + s.getKodeShift() + " = " + s.getKodeShift() +
                    " (" + s.getJamMulai() + " - " + s.getJamSelesai() + "), Kuota: " + s.getKuotaKebutuhan())
                    .setFontSize(9));
            }

            // Footer
            doc.add(new Paragraph("\nDibuat pada: " + LocalDate.now().format(DATE_FORMAT))
                .setFontSize(8)
                .setTextAlignment(TextAlignment.RIGHT)
                .setMarginTop(10));
        }
    }

    /**
     * Helper untuk membuat cell header tabel PDF.
     */
    private Cell createHeaderCell(String text, DeviceRgb bgColor) {
        return new Cell()
            .add(new Paragraph(text).setFontSize(9).setBold())
            .setBackgroundColor(bgColor)
            .setFontColor(ColorConstants.WHITE)
            .setTextAlignment(TextAlignment.CENTER);
    }
}
