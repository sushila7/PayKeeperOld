package com.bishram.payment.keeper.models;

public class RentPaidReceived {
    private String dateOfPayment;
    private String amountPaidReceived;
    private String personPaid;
    private String personReceived;
    private String pushUid;

    public RentPaidReceived() {
    }

    public RentPaidReceived(String dateOfPayment, String amountPaidReceived, String personPaid, String personReceived, String pushUid) {
        this.dateOfPayment = dateOfPayment;
        this.amountPaidReceived = amountPaidReceived;
        this.personPaid = personPaid;
        this.personReceived = personReceived;
        this.pushUid = pushUid;
    }

    public String getDateOfPayment() {
        return dateOfPayment;
    }

    public String getAmountPaidReceived() {
        return amountPaidReceived;
    }

    public String getPersonPaid() {
        return personPaid;
    }

    public String getPersonReceived() {
        return personReceived;
    }

    public String getPushUid() {
        return pushUid;
    }
}
