package com.akshit.akshitsfdc.allpuranasinhindi.models;

public class HardCopyBooks {

    private HardCopyDB hardCopyDB;

    public HardCopyBooks() {
    }

    public HardCopyBooks(HardCopyDB hardCopyDB) {
        this.setHardCopyDB(hardCopyDB);
    }


    public HardCopyDB getHardCopyDB() {
        return hardCopyDB;
    }

    public void setHardCopyDB(HardCopyDB hardCopyDB) {
        this.hardCopyDB = hardCopyDB;
    }
}
