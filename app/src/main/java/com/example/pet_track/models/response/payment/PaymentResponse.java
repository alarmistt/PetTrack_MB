package com.example.pet_track.models.response.payment;
import com.google.gson.annotations.SerializedName;

public class PaymentResponse {
        @SerializedName("bin")
        private String bin;

        @SerializedName("accountNumber")
        private String accountNumber;

        @SerializedName("amount")
        private int amount;

        @SerializedName("description")
        private String description;

        @SerializedName("orderCode")
        private long orderCode;

        @SerializedName("currency")
        private String currency;

        @SerializedName("paymentLinkId")
        private String paymentLinkId;

        @SerializedName("status")
        private String status;

        @SerializedName("expiredAt")
        private long expiredAt;

        @SerializedName("checkoutUrl")
        private String checkoutUrl;

        @SerializedName("qrCode")
        private String qrCode;

    public String getBin() {
        return bin;
    }

    public void setBin(String bin) {
        this.bin = bin;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(long orderCode) {
        this.orderCode = orderCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getPaymentLinkId() {
        return paymentLinkId;
    }

    public void setPaymentLinkId(String paymentLinkId) {
        this.paymentLinkId = paymentLinkId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(long expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getCheckoutUrl() {
        return checkoutUrl;
    }

    public void setCheckoutUrl(String checkoutUrl) {
        this.checkoutUrl = checkoutUrl;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}
