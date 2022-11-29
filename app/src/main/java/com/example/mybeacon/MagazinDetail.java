package com.example.mybeacon;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.minew.beaconplus.sdk.MTCentralManager;
import com.minew.beaconplus.sdk.MTConnectionHandler;
import com.minew.beaconplus.sdk.MTPeripheral;
import com.minew.beaconplus.sdk.enums.TriggerType;
import com.minew.beaconplus.sdk.enums.Version;
import com.minew.beaconplus.sdk.exception.MTException;
import com.minew.beaconplus.sdk.interfaces.SetTriggerListener;
import com.minew.beaconplus.sdk.model.Trigger;

public class MagazinDetail extends AppCompatActivity implements View.OnClickListener {
    private MTPeripheral mtPeripheral;
    private MTConnectionHandler mMTConnectionHandler;
    private MTCentralManager mMtCentralManager;
    private View ivBack;
    private TextView tvSet;

    int mCurSlot = 2;//Need to configure the value of the channel, 2 represents the third channel

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        initView();
        initData();
        initListener();
    }


    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        tvSet = findViewById(R.id.tv_set);

    }

    private void initData() {
        mtPeripheral = MainActivity.mtPeripheral;
        mMTConnectionHandler = mtPeripheral.mMTConnectionHandler;
        mMtCentralManager = MTCentralManager.getInstance(this);
    }

    private void initListener() {
        ivBack.setOnClickListener(this);
        tvSet.setOnClickListener(this);
    }


    public void saveTrigger() {
        Version version = this.mMTConnectionHandler.mTConnectionFeature.getVersion();
        if (version.getValue() >= 4) {
            if (this.mMTConnectionHandler.mTConnectionFeature.supportTriggers.size() > 0
                    && this.mMTConnectionHandler.triggers.size() > 0) {
                Trigger trigger = new Trigger();
                trigger.setCurSlot(mCurSlot);//Choose which channel to set
                boolean isOpen = true; //Indicates whether the trigger is enabled
                if (isOpen) {
                    TriggerType triggerType = TriggerType.BTN_DTAP_EVT;
                    trigger.setTriggerType(TriggerType.BTN_DTAP_EVT);//double click button
                    switch (triggerType) {
                        case TEMPERATURE_ABOVE_ALARM:
                        case TEMPERATURE_BELOW_ALARM:
                        case HUMIDITY_ABOVE_ALRM:
                        case HUMIDITY_BELOW_ALRM:
                        case LIGHT_ABOVE_ALRM:
                        case LIGHT_BELOW_ALARM:
                        case FORCE_ABOVE_ALRM:
                        case FORCE_BELOW_ALRM:
                        case TVOC_ABOVE_ALARM:
                        case TVOC_BELOW_ALARM:// TVOC lower than
                            //For these trigger conditions, the duration of mTemCondition does not need to be multiplied by 1000
                            trigger.setCondition(10);
                            break;
                        default:
                            trigger.setCondition(10 * 1000);
                    }
                } else {
                    trigger.setTriggerType(TriggerType.TRIGGER_SRC_NONE);
                    trigger.setCondition(0);
                }
                if (version.getValue() > 4) {
                    trigger.setAdvInterval(2000);//Broadcast interval 100 ms ~ 5000 ms
                    trigger.setRadioTxpower(0);//Broadcast power: -40dBm ~ 4dBm
                    trigger.setAlwaysAdvertising(false);//true：always broadcast, false: not always broadcast
                }

                /*TextView txt=findViewById(R.id.tv_set);
                txt.setText(this.mMTConnectionHandler.triggers);*/

                this.mMTConnectionHandler.setTriggerCondition(trigger, new SetTriggerListener() {
                    @Override
                    public void onSetTrigger(boolean success, MTException mtException) {
                        //Monitor whether the write is successful

                        Log.e("minew_tag","trigger success " + success);
                    }
                });
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                mMtCentralManager.disconnect(mtPeripheral);
                finish();
                break;
            case R.id.tv_set:
                saveTrigger();
                break;
            default:
                break;
        }
    }
}
