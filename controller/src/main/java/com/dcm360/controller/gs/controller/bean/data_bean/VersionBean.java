package com.dcm360.controller.gs.controller.bean.data_bean;

public class VersionBean {

    /**
     * msg : successed
     * data : {"diskAvailable":10,"productId":"GS-0011-0001-2501-0781","laser_serial_number":"19420801","modelType":"GS-SR-CW-571","systemVersion":"14.04","version":"GS-Conbox-06-NJ-571_V2-4-26","diskCapacity":27,"hardwareVersion0":"r20.4.14","hardwareVersion1":"r6.21.6","hardwareVersion2":"r4.1.10","hardwareVersion3":"t0.0.0","hardwareVersion4":"r1.0.0","minAppVersion":"1.9.60","softwareVersion":"GS-Conbox-06-NJ-571_V2-4-26"}
     * successed : true
     * errorCode :
     */
    private String msg;
    private DataEntity data;
    private boolean successed;
    private String errorCode;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public void setSuccessed(boolean successed) {
        this.successed = successed;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public DataEntity getData() {
        return data;
    }

    public boolean isSuccessed() {
        return successed;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public class DataEntity {
        /**
         * diskAvailable : 10
         * productId : GS-0011-0001-2501-0781
         * laser_serial_number : 19420801
         * modelType : GS-SR-CW-571
         * systemVersion : 14.04
         * version : GS-Conbox-06-NJ-571_V2-4-26
         * diskCapacity : 27
         * hardwareVersion0 : r20.4.14
         * hardwareVersion1 : r6.21.6
         * hardwareVersion2 : r4.1.10
         * hardwareVersion3 : t0.0.0
         * hardwareVersion4 : r1.0.0
         * minAppVersion : 1.9.60
         * softwareVersion : GS-Conbox-06-NJ-571_V2-4-26
         */
        private int diskAvailable;
        private String productId;
        private String laser_serial_number;
        private String modelType;
        private String systemVersion;
        private String version;
        private int diskCapacity;
        private String hardwareVersion0;
        private String hardwareVersion1;
        private String hardwareVersion2;
        private String hardwareVersion3;
        private String hardwareVersion4;
        private String minAppVersion;
        private String softwareVersion;

        public void setDiskAvailable(int diskAvailable) {
            this.diskAvailable = diskAvailable;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public void setLaser_serial_number(String laser_serial_number) {
            this.laser_serial_number = laser_serial_number;
        }

        public void setModelType(String modelType) {
            this.modelType = modelType;
        }

        public void setSystemVersion(String systemVersion) {
            this.systemVersion = systemVersion;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public void setDiskCapacity(int diskCapacity) {
            this.diskCapacity = diskCapacity;
        }

        public void setHardwareVersion0(String hardwareVersion0) {
            this.hardwareVersion0 = hardwareVersion0;
        }

        public void setHardwareVersion1(String hardwareVersion1) {
            this.hardwareVersion1 = hardwareVersion1;
        }

        public void setHardwareVersion2(String hardwareVersion2) {
            this.hardwareVersion2 = hardwareVersion2;
        }

        public void setHardwareVersion3(String hardwareVersion3) {
            this.hardwareVersion3 = hardwareVersion3;
        }

        public void setHardwareVersion4(String hardwareVersion4) {
            this.hardwareVersion4 = hardwareVersion4;
        }

        public void setMinAppVersion(String minAppVersion) {
            this.minAppVersion = minAppVersion;
        }

        public void setSoftwareVersion(String softwareVersion) {
            this.softwareVersion = softwareVersion;
        }

        public int getDiskAvailable() {
            return diskAvailable;
        }

        public String getProductId() {
            return productId;
        }

        public String getLaser_serial_number() {
            return laser_serial_number;
        }

        public String getModelType() {
            return modelType;
        }

        public String getSystemVersion() {
            return systemVersion;
        }

        public String getVersion() {
            return version;
        }

        public int getDiskCapacity() {
            return diskCapacity;
        }

        public String getHardwareVersion0() {
            return hardwareVersion0;
        }

        public String getHardwareVersion1() {
            return hardwareVersion1;
        }

        public String getHardwareVersion2() {
            return hardwareVersion2;
        }

        public String getHardwareVersion3() {
            return hardwareVersion3;
        }

        public String getHardwareVersion4() {
            return hardwareVersion4;
        }

        public String getMinAppVersion() {
            return minAppVersion;
        }

        public String getSoftwareVersion() {
            return softwareVersion;
        }
    }
}
