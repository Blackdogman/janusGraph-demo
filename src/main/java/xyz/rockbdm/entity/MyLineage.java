package xyz.rockbdm.entity;

import xyz.rockbdm.annotation.JGVertex;
import xyz.rockbdm.annotation.JGVertexField;

@JGVertex(value = "Lineage")
public class MyLineage {
    @JGVertexField(isPrimary = true)
    private String id;
    @JGVertexField
    private String lineageId;
    @JGVertexField
    private String targetInstId;
    @JGVertexField
    private String targetClassId;
    @JGVertexField
    private String targetSysId;
    @JGVertexField
    private String sourceInstId;
    @JGVertexField
    private String sourceClassId;
    @JGVertexField
    private String sourceSysId;
    @JGVertexField
    private String srcInstId;
    @JGVertexField
    private String srcType;

    @JGVertexField
    private Integer orderNum;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLineageId() {
        return lineageId;
    }

    public void setLineageId(String lineageId) {
        this.lineageId = lineageId;
    }

    public String getTargetInstId() {
        return targetInstId;
    }

    public void setTargetInstId(String targetInstId) {
        this.targetInstId = targetInstId;
    }

    public String getTargetClassId() {
        return targetClassId;
    }

    public void setTargetClassId(String targetClassId) {
        this.targetClassId = targetClassId;
    }

    public String getTargetSysId() {
        return targetSysId;
    }

    public void setTargetSysId(String targetSysId) {
        this.targetSysId = targetSysId;
    }

    public String getSourceInstId() {
        return sourceInstId;
    }

    public void setSourceInstId(String sourceInstId) {
        this.sourceInstId = sourceInstId;
    }

    public String getSourceClassId() {
        return sourceClassId;
    }

    public void setSourceClassId(String sourceClassId) {
        this.sourceClassId = sourceClassId;
    }

    public String getSourceSysId() {
        return sourceSysId;
    }

    public void setSourceSysId(String sourceSysId) {
        this.sourceSysId = sourceSysId;
    }

    public String getSrcInstId() {
        return srcInstId;
    }

    public void setSrcInstId(String srcInstId) {
        this.srcInstId = srcInstId;
    }

    public String getSrcType() {
        return srcType;
    }

    public void setSrcType(String srcType) {
        this.srcType = srcType;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "MdLineage{" +
                "id='" + id + '\'' +
                ", lineageId='" + lineageId + '\'' +
                ", targetInstId='" + targetInstId + '\'' +
                ", targetClassId='" + targetClassId + '\'' +
                ", targetSysId='" + targetSysId + '\'' +
                ", sourceInstId='" + sourceInstId + '\'' +
                ", sourceClassId='" + sourceClassId + '\'' +
                ", sourceSysId='" + sourceSysId + '\'' +
                ", srcInstId='" + srcInstId + '\'' +
                ", srcType='" + srcType + '\'' +
                ", orderNum=" + orderNum +
                '}';
    }
}
