package info.blockchain.ui;

import android.support.v7.widget.RecyclerView;
import android.test.ActivityInstrumentationTestCase2;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;

import com.robotium.solo.Solo;

import junit.framework.TestCase;

import java.text.DecimalFormat;
import java.util.ArrayList;

import info.blockchain.wallet.MainActivity;
import info.blockchain.wallet.util.ExchangeRateFactory;
import info.blockchain.wallet.util.MonetaryUtil;
import info.blockchain.wallet.util.PrefsUtil;
import piuk.blockchain.android.R;

public class BalanceScreenTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo = null;

    RecyclerView txList = null;

    public BalanceScreenTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {

        solo = new Solo(getInstrumentation(), getActivity());
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
    }

    //Enter pin might only be needed if running BalanceScreenTest individually
    public void testAAA_enterPin() throws AssertionError {

        String pin = solo.getString(R.string.qa_test_pin1);

        ArrayList<Integer> pinSequence = new ArrayList<>();
        pinSequence.add(Integer.parseInt(pin.substring(0, 1)));
        pinSequence.add(Integer.parseInt(pin.substring(1, 2)));
        pinSequence.add(Integer.parseInt(pin.substring(2, 3)));
        pinSequence.add(Integer.parseInt(pin.substring(3, 4)));

        for(int i : pinSequence){

            switch (i){
                case 0:solo.clickOnView(solo.getView(R.id.button0));break;
                case 1:solo.clickOnView(solo.getView(R.id.button1));break;
                case 2:solo.clickOnView(solo.getView(R.id.button2));break;
                case 3:solo.clickOnView(solo.getView(R.id.button3));break;
                case 4:solo.clickOnView(solo.getView(R.id.button4));break;
                case 5:solo.clickOnView(solo.getView(R.id.button5));break;
                case 6:solo.clickOnView(solo.getView(R.id.button6));break;
                case 7:solo.clickOnView(solo.getView(R.id.button7));break;
                case 8:solo.clickOnView(solo.getView(R.id.button8));break;
                case 9:solo.clickOnView(solo.getView(R.id.button9));break;
            }
            try{solo.sleep(500);}catch (Exception e){}
        }
        solo.waitForView(solo.getView(R.id.balance1));
    }

    public void testA_ChangeCurrencyTapBalance() throws AssertionError{

        TextView balance = (TextView)solo.getView(R.id.balance1);
        String btc = balance.getText().toString();

        //Set default fiat, btc
        PrefsUtil.getInstance(solo.getCurrentActivity()).setValue(PrefsUtil.KEY_SELECTED_FIAT, PrefsUtil.DEFAULT_CURRENCY);
        PrefsUtil.getInstance(solo.getCurrentActivity()).setValue(PrefsUtil.KEY_BTC_UNITS, MonetaryUtil.UNIT_BTC);

        String strFiat = PrefsUtil.getInstance(solo.getCurrentActivity()).getValue(PrefsUtil.KEY_SELECTED_FIAT, PrefsUtil.DEFAULT_CURRENCY);
        double btc_fx = ExchangeRateFactory.getInstance(solo.getCurrentActivity()).getLastPrice(strFiat);
        double fiat_balance = btc_fx * Double.parseDouble(btc.split(" ")[0]);

        solo.clickOnView(balance);

        DecimalFormat df = new DecimalFormat("#.##");
        String fiat = balance.getText().toString();

        //Test if btc converts to correct fiat
        TestCase.assertTrue(fiat.split(" ")[0].equals(df.format(fiat_balance)));
    }

    public void testB_ChangeCurrencyTapTxAmount() throws AssertionError{

        TextView balance = (TextView)solo.getView(R.id.balance1);
        String btc = balance.getText().toString();

        //Set default fiat, btc
        PrefsUtil.getInstance(solo.getCurrentActivity()).setValue(PrefsUtil.KEY_SELECTED_FIAT, PrefsUtil.DEFAULT_CURRENCY);
        PrefsUtil.getInstance(solo.getCurrentActivity()).setValue(PrefsUtil.KEY_BTC_UNITS, MonetaryUtil.UNIT_BTC);

        String strFiat = PrefsUtil.getInstance(solo.getCurrentActivity()).getValue(PrefsUtil.KEY_SELECTED_FIAT, PrefsUtil.DEFAULT_CURRENCY);
        double btc_fx = ExchangeRateFactory.getInstance(solo.getCurrentActivity()).getLastPrice(strFiat);
        double fiat_balance = btc_fx * Double.parseDouble(btc.split(" ")[0]);

        txList = (RecyclerView)solo.getView(R.id.txList2);
        solo.clickOnView(txList.getChildAt(0).findViewById(R.id.result));

        DecimalFormat df = new DecimalFormat("#.##");
        String fiat = balance.getText().toString();

        //Test if btc converts to correct fiat
        TestCase.assertTrue(fiat.split(" ")[0].equals(df.format(fiat_balance)));
    }

    public void testC_BasicUI() throws AssertionError{

        Spinner mSpinner = solo.getView(Spinner.class, 0);
        int itemCount = mSpinner.getAdapter().getCount();
        View spinnerView = solo.getView(Spinner.class, 0);

        for(int i = itemCount-1; i >= 0; i--){

            solo.clickOnView(spinnerView);
            solo.scrollToTop(); // I put this in here so that it always keeps the list at start
            solo.clickOnView(solo.getView(TextView.class, i));
            try{solo.sleep(1000);}catch (Exception e){}

            txList = (RecyclerView) solo.getView(R.id.txList2);
            if(txList.getAdapter().getItemCount()==0)continue;

            //Test if expanded
            solo.clickOnView(txList.getChildAt(0));
            TestCase.assertTrue(solo.waitForText(solo.getString(R.string.from)) && solo.waitForText(solo.getString(R.string.transaction_fee)));

            //Toggle status/confirmation amount
            solo.clickOnView(solo.getView(R.id.transaction_status));
            solo.clickOnView(solo.getView(R.id.transaction_status));
            solo.clickOnText(solo.getString(R.string.from));

            //Test scrolling
            txList.fling(0, 20000);
            try{solo.sleep(1000);}catch (Exception e){}
            txList.fling(0, -20000);
            try{solo.sleep(1000);}catch (Exception e){}
        }

        //Test hash opens link
        if(txList.getAdapter().getItemCount()>0) {
            solo.clickOnView(txList.getChildAt(0));
            solo.clickOnView(solo.getView(R.id.tx_hash));
        }
    }
}