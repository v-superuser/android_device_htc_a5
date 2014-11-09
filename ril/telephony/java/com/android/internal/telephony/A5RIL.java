package com.android.internal.telephony;

import android.content.Context;
import android.os.AsyncResult;
import android.os.Message;

public class A5RIL extends RIL implements CommandsInterface {

    private static final int RIL_REQUEST_SIM_TRANSMIT_APDU_BASIC = 5505;
    private static final int RIL_REQUEST_SIM_OPEN_CHANNEL = 5506;
    private static final int RIL_REQUEST_SIM_CLOSE_CHANNEL = 5507;
    private static final int RIL_UNSOL_HARDWARE_CONFIG_CHANGED = 5760;

    public A5RIL(Context paramContext, int paramInt1,
           int paramInt2, Integer paramInteger) {
         super(paramContext, paramInt1, paramInt2, paramInteger);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iccTransmitApduBasicChannel(int cla, int instruction, int p1, int p2,
            int p3, String data, Message response) {
        iccTransmitApduHelper(RIL_REQUEST_SIM_TRANSMIT_APDU_BASIC, 0, cla, instruction,
                p1, p2, p3, data, response);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iccOpenLogicalChannel(String AID, Message response) {
        if(mRilVersion < 10) {
            if (response != null) {
                CommandException ex = new CommandException(
                    CommandException.Error.REQUEST_NOT_SUPPORTED);
                AsyncResult.forMessage(response, null, ex);
                response.sendToTarget();
            }
            return;
        }

        RILRequest rr = RILRequest.obtain(RIL_REQUEST_SIM_OPEN_CHANNEL, response);
        rr.mParcel.writeString(AID);

        if (RILJ_LOGD)
            riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

        send(rr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void iccCloseLogicalChannel(int channel, Message response) {
        RILRequest rr = RILRequest.obtain(RIL_REQUEST_SIM_CLOSE_CHANNEL, response);
        rr.mParcel.writeInt(1);
        rr.mParcel.writeInt(channel);

        if (RILJ_LOGD)
            riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

        send(rr);
    }

    /*
     * Helper function for the iccTransmitApdu* commands above.
     */
    private void iccTransmitApduHelper(int rilCommand, int channel, int cla,
            int instruction, int p1, int p2, int p3, String data, Message response) {

        if(mRilVersion < 10) {
            if (response != null) {
                CommandException ex = new CommandException(
                    CommandException.Error.REQUEST_NOT_SUPPORTED);
                AsyncResult.forMessage(response, null, ex);
                response.sendToTarget();
            }
            return;
        }

        RILRequest rr = RILRequest.obtain(rilCommand, response);
        rr.mParcel.writeInt(channel);
        rr.mParcel.writeInt(cla);
        rr.mParcel.writeInt(instruction);
        rr.mParcel.writeInt(p1);
        rr.mParcel.writeInt(p2);
        rr.mParcel.writeInt(p3);
        rr.mParcel.writeString(data);

        if (RILJ_LOGD)
            riljLog(rr.serialString() + "> " + requestToString(rr.mRequest));

        send(rr);
    }

    static String
    requestToString(int request) {
        switch (request) {
            case RIL_REQUEST_SIM_TRANSMIT_APDU_BASIC: return "RIL_REQUEST_SIM_TRANSMIT_APDU_BASIC";
            case RIL_REQUEST_SIM_OPEN_CHANNEL: return "RIL_REQUEST_SIM_OPEN_CHANNEL";
            case RIL_REQUEST_SIM_CLOSE_CHANNEL: return "RIL_REQUEST_SIM_CLOSE_CHANNEL";
            case RIL_UNSOL_HARDWARE_CONFIG_CHANGED: return "RIL_UNSOL_HARDWARE_CONFIG_CHANGED";
            default: return "<unknown request>";
        }
    }

}
