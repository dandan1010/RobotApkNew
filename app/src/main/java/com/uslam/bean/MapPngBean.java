package com.uslam.bean;

public class MapPngBean {

    private String mapName;
    private boolean png_map;
    private boolean will_load;
    private boolean umap;
    private boolean archive;

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public boolean isPng_map() {
        return png_map;
    }

    public void setPng_map(boolean png_map) {
        this.png_map = png_map;
    }

    public boolean isWill_load() {
        return will_load;
    }

    public void setWill_load(boolean will_load) {
        this.will_load = will_load;
    }

    public boolean isUmap() {
        return umap;
    }

    public void setUmap(boolean umap) {
        this.umap = umap;
    }

    public boolean isArchive() {
        return archive;
    }

    public void setArchive(boolean archive) {
        this.archive = archive;
    }
}
