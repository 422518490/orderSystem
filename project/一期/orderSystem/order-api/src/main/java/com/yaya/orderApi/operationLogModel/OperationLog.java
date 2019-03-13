package com.yaya.orderapi.operationLogModel;

import java.io.Serializable;
import java.util.Date;

public class OperationLog implements Serializable {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operation_log.OPERATION_ID
     *
     * @mbggenerated
     */
    private String operationId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operation_log.OPERATION_TYPE
     *
     * @mbggenerated
     */
    private String operationType;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operation_log.FROM_ID
     *
     * @mbggenerated
     */
    private String fromId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operation_log.TO_ID
     *
     * @mbggenerated
     */
    private String toId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operation_log.OPERATION_DESC
     *
     * @mbggenerated
     */
    private String operationDesc;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column operation_log.CREATE_TIME
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table operation_log
     *
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.OPERATION_ID
     *
     * @return the value of operation_log.OPERATION_ID
     *
     * @mbggenerated
     */
    public String getOperationId() {
        return operationId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.OPERATION_ID
     *
     * @param operationId the value for operation_log.OPERATION_ID
     *
     * @mbggenerated
     */
    public void setOperationId(String operationId) {
        this.operationId = operationId == null ? null : operationId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.OPERATION_TYPE
     *
     * @return the value of operation_log.OPERATION_TYPE
     *
     * @mbggenerated
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.OPERATION_TYPE
     *
     * @param operationType the value for operation_log.OPERATION_TYPE
     *
     * @mbggenerated
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType == null ? null : operationType.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.FROM_ID
     *
     * @return the value of operation_log.FROM_ID
     *
     * @mbggenerated
     */
    public String getFromId() {
        return fromId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.FROM_ID
     *
     * @param fromId the value for operation_log.FROM_ID
     *
     * @mbggenerated
     */
    public void setFromId(String fromId) {
        this.fromId = fromId == null ? null : fromId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.TO_ID
     *
     * @return the value of operation_log.TO_ID
     *
     * @mbggenerated
     */
    public String getToId() {
        return toId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.TO_ID
     *
     * @param toId the value for operation_log.TO_ID
     *
     * @mbggenerated
     */
    public void setToId(String toId) {
        this.toId = toId == null ? null : toId.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.OPERATION_DESC
     *
     * @return the value of operation_log.OPERATION_DESC
     *
     * @mbggenerated
     */
    public String getOperationDesc() {
        return operationDesc;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.OPERATION_DESC
     *
     * @param operationDesc the value for operation_log.OPERATION_DESC
     *
     * @mbggenerated
     */
    public void setOperationDesc(String operationDesc) {
        this.operationDesc = operationDesc == null ? null : operationDesc.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column operation_log.CREATE_TIME
     *
     * @return the value of operation_log.CREATE_TIME
     *
     * @mbggenerated
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column operation_log.CREATE_TIME
     *
     * @param createTime the value for operation_log.CREATE_TIME
     *
     * @mbggenerated
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table operation_log
     *
     * @mbggenerated
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", operationId=").append(operationId);
        sb.append(", operationType=").append(operationType);
        sb.append(", fromId=").append(fromId);
        sb.append(", toId=").append(toId);
        sb.append(", operationDesc=").append(operationDesc);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}