package com.raisesail.andoid.androidupload;

public class RaiseData {
    /**
     * meta : {"additional information":"some data"}
     * name : image_03325512
     * dataFormat : jpeg
     * data :
     */

    private MetaBean meta;
    private String name;
    private String dataFormat;
    private String data;

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(String dataFormat) {
        this.dataFormat = dataFormat;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public static class MetaBean {

        private String serialNumber;

        public String getSerialNumber() {
            return serialNumber;
        }
        public void setSerialNumber(String serialNumber) {
            this.serialNumber = serialNumber;
        }
    }
}
