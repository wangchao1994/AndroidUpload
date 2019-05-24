package com.raisesail.andoid.androidupload;


public class ResponseData {
    /**
     * status : OK
     * id :
     * link :
     * meta : {}
     */

    private String status;
    private String id;
    private String link;
    private MetaBean meta;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public MetaBean getMeta() {
        return meta;
    }

    public void setMeta(MetaBean meta) {
        this.meta = meta;
    }

    public static class MetaBean {
    }
}
