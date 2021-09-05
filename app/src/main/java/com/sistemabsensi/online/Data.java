package com.sistemabsensi.online;

public class Data {
    private String id_absen;
    private String nama;
    private String keterangan;
    private String tanggal;
    private String waktu;
    private String statusabsensi;
    private String nip;

    public String getId_absen() {
        return id_absen;
    }

    public void setId_absen(String id_absen) {
        this.id_absen = id_absen;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getStatusabsensi() {
        return statusabsensi;
    }

    public void setStatusabsensi(String statusabsensi) {
        this.statusabsensi = statusabsensi;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public Data() {
        this.id_absen = id_absen;
        this.nama = nama;
        this.keterangan = keterangan;
        this.tanggal = tanggal;
        this.waktu = waktu;
        this.statusabsensi = statusabsensi;
        this.nip = nip;
    }
}

